package com.atguigu.guli.service.edu.controller.admin;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author Helen
 * @since 2023-03-01
 */
// // @CrossOrigin
@Api(tags = "课时管理")
@RestController
@RequestMapping("/admin/edu/video")
@Slf4j
public class VideoController {

    @Resource
    private VideoService videoService;

    @ApiOperation("新增课时")
    @PostMapping("save")
    public R save(@ApiParam(value = "课时对象", required = true)
                  @RequestBody Video video) {
        boolean result = videoService.save(video);
        if (result) {
            return R.ok().message("保存成功");
        } else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation("根据id查询课时")
    @GetMapping("get/{id}")
    public R getById(@ApiParam(value = "课时id", required = true)
                     @PathVariable String id) {
        Video video = videoService.getById(id);
        if (video != null) {
            return R.ok().data("item", video);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据id修改课时")
    @PutMapping("update")
    public R updateById(@ApiParam(value = "课时对象", required = true)
                        @RequestBody Video video) {
        boolean result = videoService.updateById(video);
        if (result) {
            return R.ok().message("修改成功；；");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据ID删除课时")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "课时ID", required = true)
                        @PathVariable String id) {
        // 删除视频
        videoService.removeMediaVideoById(id);
        // 在此处调用vod中的删除视频文件的接口
        boolean result = videoService.removeById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }
}

