package com.atguigu.guli.service.edu.service.impl;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.entity.*;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.*;
import com.atguigu.guli.service.edu.feign.OssFileService;
import com.atguigu.guli.service.edu.mapper.*;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2023-03-01
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Resource
    private CourseDescriptionMapper courseDescriptionMapper;
    @Resource
    private VideoMapper videoMapper;
    @Resource
    private ChapterMapper chapterMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private CourseCollectMapper courseCollectMapper;
    @Resource
    private OssFileService ossFileService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        course.setStatus(Course.COURSE_DRAFT);
        baseMapper.insert(course);

        CourseDescription courseDescription = new CourseDescription();
        courseDescription
                .setDescription(courseInfoForm.getDescription())
                .setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);

        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        Course course = baseMapper.selectById(id);
        if (course == null) {
            return null;
        }

        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);

        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        courseInfoForm.setDescription(courseDescription.getDescription());

        return courseInfoForm;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        // 保存课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        // 保存课程详情信息
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo) {
        QueryWrapper<CourseVo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("c.gmt_create");

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();

        wrapper.like(!StringUtils.isEmpty(title), "c.title", title);
        wrapper.eq(!StringUtils.isEmpty(teacherId), "c.teacher_id", teacherId);
        wrapper.eq(!StringUtils.isEmpty(subjectParentId), "c.subject_parent_id", subjectParentId);
        wrapper.eq(!StringUtils.isEmpty(subjectId), "c.subject_id", subjectId);

        Page<CourseVo> pageParam = new Page<>(page, limit);
        // 放入分页参数和查询条件参数，mp会自动组装
        List<CourseVo> records = baseMapper.selectPageByCourseQueryVo(pageParam, wrapper);

        return pageParam.setRecords(records);
    }

    @Override
    public boolean removeCoverById(String id) {
        Course course = baseMapper.selectById(id);
        if (course != null) {
            String cover = course.getCover();
            if (!StringUtils.isEmpty(cover)) {
                // 删除图片
                R r = ossFileService.removeFile(cover);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {
        // 收藏信息：course_collect
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq("course_id", id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        // 评论信息：comment
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id", id);
        commentMapper.delete(commentQueryWrapper);

        // 课时信息：video
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", id);
        videoMapper.delete(videoQueryWrapper);

        // 章节信息：chapter
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", id);
        chapterMapper.delete(chapterQueryWrapper);

        // 课程详情：course_description
        courseDescriptionMapper.deleteById(id);

        // 课程信息：course
        return this.removeById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        return this.updateById(course);
    }

    @Override
    public List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        // 查询已发布的课程
        queryWrapper.eq("status", Course.COURSE_NORMAL);

        String subjectParentId = webCourseQueryVo.getSubjectParentId();
        String subjectId = webCourseQueryVo.getSubjectId();
        String buyCountSort = webCourseQueryVo.getBuyCountSort();
        String gmtCreateSort = webCourseQueryVo.getGmtCreateSort();
        String priceSort = webCourseQueryVo.getPriceSort();
        Integer type = webCourseQueryVo.getType();

        queryWrapper.eq(!StringUtils.isEmpty(subjectParentId), "subject_parent_id", subjectParentId);
        queryWrapper.eq(!StringUtils.isEmpty(subjectId), "subject_id", subjectId);
        queryWrapper.orderByDesc(!StringUtils.isEmpty(buyCountSort), "buy_count");
        queryWrapper.orderByDesc(!StringUtils.isEmpty(gmtCreateSort), "gmt_create");

        if (!StringUtils.isEmpty(priceSort)) {
            if (type == null || type == 1) {
                queryWrapper.orderByAsc("price");
            } else {
                queryWrapper.orderByDesc("price");
            }
        }

        return baseMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        // 更新课程浏览数
        Course course = baseMapper.selectById(id);
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);
        // 获取课程信息
        return baseMapper.selectWebCourseVoById(id);
    }

    @Override
    @Cacheable(value = "index", key = "'selectHotCourse'")
    public List<Course> selectHotCourse() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view_count");
        queryWrapper.last("limit 8");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public CourseDto getCourseDtoById(String courseId) {
        return baseMapper.selectCourseDtoById(courseId);
    }

    @Override
    public void updateBuyCountById(String id) {
        Course course = baseMapper.selectById(id);
        course.setBuyCount(course.getBuyCount() + 1);
        this.updateById(course);
    }
}
