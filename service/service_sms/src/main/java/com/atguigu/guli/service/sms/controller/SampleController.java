package com.atguigu.guli.service.sms.controller;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.sms.util.SmsProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sms/sample")
@RefreshScope
public class SampleController {
    @Value("${aliyun.sms.signName}")
    private String signName;
    @Resource
    private SmsProperties smsProperties;

    @GetMapping("test1")
    public R test1() {
        return R.ok().data("signName", signName);
    }

    @GetMapping("test2")
    public R test2() {
        return R.ok().data("smsProperties", smsProperties);
    }
}