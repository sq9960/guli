package com.atguigu.guli.service.edu.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.atguigu.guli.service.edu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

// @CrossOrigin
@Api(tags = "课程")
@RestController
@RequestMapping("/api/edu/course")
public class ApiCourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private ChapterService chapterService;

    @ApiOperation("课程列表")
    @GetMapping("list")
    public R list(@ApiParam(value = "查询对象", required = false)
                  WebCourseQueryVo webCourseQueryVo) {
        List<Course> courseList = courseService.webSelectList(webCourseQueryVo);
        return R.ok().data("courseList", courseList);
    }

    @ApiOperation("根据ID查询课程")
    @GetMapping("get/{courseId}")
    public R getById(@ApiParam(value = "课程id", required = true)
                     @PathVariable String courseId) {
        // 查询课程信息和讲师信息
        WebCourseVo webCourseVo = courseService.selectWebCourseVoById(courseId);

        // 查询当前课程的章节信息
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);

        return R.ok().data("course", webCourseVo).data("chapterVoList", chapterVoList);
    }

    @ApiOperation("根据课程id查询课程信息")
    @GetMapping("inner/get-course-dto/{courseId}")
    public CourseDto getCourseDtoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId) {
        return courseService.getCourseDtoById(courseId);
    }

    @ApiOperation("根据课程id更改销售量")
    @GetMapping("inner/update-buy-count/{id}")
    public R updateBuyCountById(@ApiParam(value = "课程id", required = true)
                                @PathVariable String id) {
        courseService.updateBuyCountById(id);
        return R.ok();
    }
}