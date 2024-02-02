package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@Slf4j
@RequestMapping("user/status")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String KEY = "SHOP_STATUS";
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
