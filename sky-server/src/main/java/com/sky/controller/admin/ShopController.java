package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("admin/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String KEY = "SHOP_STATUS";
    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("修改店铺状态为 {}",status == StatusConstant.ENABLE ? "营业中" : "打烊中");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY,status);
        return Result.success();
    }

    /**
     * 管理端获取店铺状态
     * @return
     */
    @GetMapping("status")
    public Result<Integer> getStatus(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(KEY);
        log.info("管理端获取店铺状态为 {}",status == StatusConstant.ENABLE ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
