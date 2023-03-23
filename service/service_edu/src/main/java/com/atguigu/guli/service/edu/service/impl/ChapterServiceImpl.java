package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.VideoVo;
import com.atguigu.guli.service.edu.mapper.ChapterMapper;
import com.atguigu.guli.service.edu.mapper.VideoMapper;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {
    @Resource
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeChapterById(String id) {
        // 课时信息：video
        QueryWrapper<Video> videoWrapper = new QueryWrapper<>();
        videoWrapper.eq("chapter_id", id);
        videoMapper.delete(videoWrapper);

        // 章节信息：chapter
        return this.removeById(id);
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {
        // 获取章信息
        QueryWrapper<Chapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id", courseId);
        chapterWrapper.orderByAsc("sort", "id");
        List<Chapter> chapterList = baseMapper.selectList(chapterWrapper);

        // 获取课时信息
        QueryWrapper<Video> videoWrapper = new QueryWrapper<>();
        videoWrapper.eq("course_id", courseId);
        videoWrapper.orderByAsc("sort", "id");
        List<Video> videoList = videoMapper.selectList(videoWrapper);

        List<ChapterVo> chapterVoList = new ArrayList<>();

        chapterList.forEach(chapter -> {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            chapterVoList.add(chapterVo);

            ArrayList<VideoVo> videoVoList = new ArrayList<>();
            videoList.stream().filter(video -> chapter.getId().equals(video.getChapterId())).forEach(video -> {
                VideoVo videoVo = new VideoVo();
                BeanUtils.copyProperties(video, videoVo);
                videoVoList.add(videoVo);
            });

            chapterVo.setChildren(videoVoList);
        });

        return chapterVoList;
    }
}
