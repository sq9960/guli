package com.atguigu.guli.service.cms.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.cms.entity.Ad;
import com.atguigu.guli.service.cms.service.AdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

// @CrossOrigin // 解决跨域问题
@Api(tags = "广告推荐")
@RestController
@RequestMapping("/api/cms/ad")
public class ApiAdController {
    @Resource
    private RedisTemplate<String, Ad> redisTemplate;
    @Resource
    private AdService adService;

    @ApiOperation("根据推荐位id显示广告推荐")
    @GetMapping("list/{adTypeId}")
    public R listByAdTypeId(@ApiParam(value = "推荐位id", required = true) @PathVariable String adTypeId) {

        List<Ad> ads = adService.selectByAdTypeId(adTypeId);
        return R.ok().data("items", ads);
    }

    @PostMapping("save-test")
    public R saveAd(@RequestBody Ad ad) {
        // redisTemplate.opsForValue().set("ad1", ad);
        redisTemplate.opsForValue().set("ad", ad);
        return R.ok();
    }

    @GetMapping("get-test/{key}")
    public R getAd(@PathVariable String key) {
        Ad ad = redisTemplate.opsForValue().get(key);
        return R.ok().data("ad", ad);
    }

    @DeleteMapping("remove-test/{key}")
    public R removeAd(@PathVariable String key) {
        Boolean delete = redisTemplate.delete(key);
        System.out.println(delete);// 是否删除成功
        Boolean hasKey = redisTemplate.hasKey(key);
        System.out.println(hasKey);// key是否存在
        return R.ok();
    }
}