package com.atguigu.guli.service.edu.controller.admin;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.atguigu.guli.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2023-03-01
 */
// // @CrossOrigin
@Api(tags = "章节管理")
@RestController
@RequestMapping("/admin/edu/chapter")
@Slf4j
public class ChapterController {
    @Resource
    private ChapterService chapterService;
    @Resource
    private VideoService videoService;

    @ApiOperation("新增章节")
    @PostMapping("save")
    public R save(@ApiParam(value = "章节对象", required = true)
                  @RequestBody Chapter chapter) {
        boolean result = chapterService.save(chapter);
        if (result) {
            return R.ok().message("保存成功");
        } else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation("根据id查询章节")
    @GetMapping("get/{id}")
    public R getById(@ApiParam(value = "章节id", required = true)
                     @PathVariable String id) {
        Chapter chapter = chapterService.getById(id);
        if (chapter != null) {
            return R.ok().data("item", chapter);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据id修改章节")
    @PutMapping("update")
    public R updateById(@ApiParam(value = "章节对象", required = true)
                        @RequestBody Chapter chapter) {
        boolean result = chapterService.updateById(chapter);
        if (result) {
            return R.ok().message("修改成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据ID删除章节")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "章节ID", required = true)
                        @PathVariable String id) {
        // 删除视频：VOD
        videoService.removeMediaVideoByChapterId(id);

        // 在此处调用vod中的删除视频文件的接口
        boolean result = chapterService.removeChapterById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("嵌套章节数据列表")
    @GetMapping("nested-list/{courseId}")
    public R nestedListByCourseId(@ApiParam(value = "课程ID", required = true)
                                  @PathVariable String courseId) {
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("items", chapterVoList);
    }
}

