package com.atguigu.guli.service.edu.controller;

import com.atguigu.guli.common.base.result.R;
import org.springframework.web.bind.annotation.*;

/**
 * The type Login controller.
 *
 * @author helen
 * @since 2020 /4/13
 */
// // @CrossOrigin
@RestController
@RequestMapping("/user")
public class LoginController {

    /**
     * 登录
     *
     * @return r
     */
    @PostMapping("login")
    public R login() {
        return R.ok().data("token", "admin");
    }

    /**
     * 获取用户信息
     *
     * @return r
     */
    @GetMapping("info")
    public R info() {
        return R.ok()
                .data("name", "admin")
                .data("roles", "[admin]")
                .data("avatar", "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
    }

    /**
     * 退出
     *
     * @return r
     */
    @PostMapping("logout")
    public R logout() {
        return R.ok();
    }
}