package com.atguigu.guli.service.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.excel.ExcelSubjectData;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor // 全参
@NoArgsConstructor // 无参
public class ExcelSubjectDataListener extends AnalysisEventListener<ExcelSubjectData> {

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private SubjectMapper subjectMapper;

    /**
     * 遍历每一行的记录
     *
     * @param data
     * @param context
     */
    @Override
    public void invoke(ExcelSubjectData data, AnalysisContext context) {
        log.info("解析到一条记录: {}", data);
        // 处理读取出来的数据
        String levelOneTitle = data.getLevelOneTitle();// 一级标题
        String levelTwoTitle = data.getLevelTwoTitle();// 二级标题
        log.info("levelOneTitle: {}", levelOneTitle);
        log.info("levelTwoTitle: {}", levelTwoTitle);

        // 判断一级分类是否重复
        Subject subjectLevelOne = this.getByTitle(levelOneTitle);
        String parentId = null;
        if (subjectLevelOne == null) {
            // 将一级分类存入数据库
            Subject subject = new Subject();
            subject.setParentId("0");
            subject.setTitle(levelOneTitle);// 一级分类名称
            subjectMapper.insert(subject);
            parentId = subject.getId();
        } else {
            parentId = subjectLevelOne.getId();
        }

        // 判断二级分类是否重复
        Subject subjectLevelTwo = this.getSubByTitle(levelTwoTitle, parentId);
        if (subjectLevelTwo == null) {
            // 将二级分类存入数据库
            Subject subject = new Subject();
            subject.setTitle(levelTwoTitle);
            subject.setParentId(parentId);
            subjectMapper.insert(subject);// 添加
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

    /**
     * 根据分类名称查询这个一级分类是否存在
     *
     * @param title
     * @return
     */
    private Subject getByTitle(String title) {

        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getTitle, title);
        wrapper.eq(Subject::getParentId, "0");// 一级分类
        return subjectMapper.selectOne(wrapper);
    }

    /**
     * 根据分类名称和父id查询这个二级分类是否存在
     *
     * @param title
     * @return
     */
    private Subject getSubByTitle(String title, String parentId) {

        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getTitle, title);
        wrapper.eq(Subject::getParentId, parentId);
        return subjectMapper.selectOne(wrapper);
    }
}
