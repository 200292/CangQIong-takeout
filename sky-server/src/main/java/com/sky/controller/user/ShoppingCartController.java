package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user/shoppingCart")
@Slf4j
/**
 * 购物车相关接口
 */
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("add")
    /**
     * 添加购物车
     */
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加到购物车,商品信息为 {}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("list")
    /**
     * 查看购物车
     */
    public Result<List<ShoppingCart>> list(){
        log.info("查看购物车");
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @PostMapping("sub")
    /**
     * 减去购物车的一个菜品
     */
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("购物车中减去一个商品,商品信息为 {}",shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 清除用户购物车中所有数据
     * @return
     */
    @DeleteMapping("clean")
    public Result clean(){
        log.info("清除购物车中的所有数据，用户id为 {}", BaseContext.getCurrentId());
        shoppingCartService.clean();
        return Result.success();
    }
}
