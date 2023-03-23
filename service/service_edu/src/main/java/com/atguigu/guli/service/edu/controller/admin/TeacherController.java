package com.atguigu.guli.service.edu.controller.admin;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.atguigu.guli.service.edu.feign.OssFileService;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "讲师管理")
@RestController
@RequestMapping("/admin/edu/teacher")
// // @CrossOrigin
@Slf4j
public class TeacherController {
    @Resource
    private TeacherService teacherService;
    @Resource
    private OssFileService ossFileService;

    @ApiOperation("所有讲师列表")
    @GetMapping("list")
    public R listAll() {
        List<Teacher> list = teacherService.list();
        return R.ok().data("items", list);
    }

    @ApiOperation(value = "根据ID删除讲师", notes = "根据ID删除讲师，逻辑删除")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam("讲师ID") @PathVariable String id) {
        boolean result = teacherService.removeById(id);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("讲师分页列表")
    @GetMapping("list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true, defaultValue = "1") @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true, defaultValue = "2") @PathVariable Long limit,
                      @ApiParam("讲师列表查询对象") TeacherQueryVo teacherQueryVo) {
        Page<Teacher> pageParam = new Page<>(page, limit);

        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String joinDateBegin = teacherQueryVo.getJoinDateBegin();
        String joinDateEnd = teacherQueryVo.getJoinDateEnd();

        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Teacher::getSort)
                .likeRight(StringUtils.isNotEmpty(name), Teacher::getName, name)
                .eq(level != null, Teacher::getLevel, level)
                .ge(StringUtils.isNotEmpty(joinDateBegin), Teacher::getJoinDate, joinDateBegin)
                .le(StringUtils.isNotEmpty(joinDateEnd), Teacher::getJoinDate, joinDateEnd);

        Page<Teacher> pageModel = teacherService.page(pageParam, wrapper);

        long total = pageModel.getTotal();
        List<Teacher> records = pageModel.getRecords();

        return R.ok().data("total", total).data("rows", records);
    }

    @ApiOperation("新增讲师")
    @PostMapping("save")
    public R save(@ApiParam("讲师对象") @RequestBody Teacher teacher) {
        teacherService.save(teacher);
        return R.ok().message("保存成功");
    }

    @ApiOperation("更新讲师")
    @PutMapping("update")
    public R update(@ApiParam("讲师对象") @RequestBody Teacher teacher) {
        boolean result = teacherService.updateById(teacher);
        if (result) {
            return R.ok().message("修改成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据ID获取讲师信息")
    @GetMapping("get/{id}")
    public R getById(@ApiParam("讲师ID") @PathVariable String id) {
        teacherService.removeAvatarById(id);

        Teacher teacher = teacherService.getById(id);
        if (teacher != null) {
            return R.ok().data("item", teacher);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据id列表删除讲师")
    @DeleteMapping("batch-remove")
    public R removeRows(@ApiParam(value = "讲师ID列表", readOnly = true) @RequestBody List<String> idList) {
        boolean result = teacherService.removeByIds(idList);
        if (result) {
            return R.ok().message("删除成功");
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据左关键字查询讲师名列表")
    @GetMapping("list/name/{key}")
    public R selectNameListByKey(
            @ApiParam(value = "查询关键字", required = true)
            @PathVariable String key) {

        List<Map<String, Object>> nameList = teacherService.selectNameListByKey(key);

        return R.ok().data("nameList", nameList);
    }

    @ApiOperation("测试服务调用")
    @GetMapping("test")
    public R test() {
        ossFileService.test();
        log.info("edu执行成功");
        return R.ok();
    }

    @ApiOperation("测试并发")
    @GetMapping("test_concurrent")
    public R testConcurrent() {
        log.info("test_concurrent");
        return R.ok();
    }

    @GetMapping("/message1")
    public String message1() {
        return "message1";
    }

    @GetMapping("/message2")
    public String message2() {
        return "message2";
    }
}
