package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("admin/dish")
@Slf4j
/**
 * 菜品管理
 */
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping()
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品 {}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清除该菜品所属分类的缓存,因为该分类的菜品不再是缓存中存储的几种了
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询 {}",dishPageQueryDTO);
        PageResult pageResult = dishService.query(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping()
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品删除 {}",ids);
        dishService.deleteBatch(ids);

        //批量删除菜品时，菜品可能对应一个分类也可能对应多个分类，想知道对应的分类还需要查询。为简化操作直接删除所有缓存
        //删除所有key为dish_*格式的缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据菜品id查询菜品及其口味
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<DishVO> getByDishId(@PathVariable Long id){
        log.info("根据菜品id查询菜品 {}",id);
        //返回的菜品需要包含口味信息，不能直接使用菜品对象返回
        DishVO dishVO = dishService.getWithFlavorById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品及其口味
     * @param dishDTO
     * @return
     */
    @PutMapping()
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品 {}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //修改菜品时可能会修改菜品的分类，会影响redis中的两条记录，简便起见直接删除所有分类-菜品的缓存
        //删除所有key为dish_*格式的缓存
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品 {}",categoryId);
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 起售停售菜品
     * @param status
     * @return
     */
    @PostMapping("status/{status}")
    public Result updateStatus(@PathVariable int status,Long id){
        log.info("修改起售停售信息,状态:{} id:{}",status,id);
        dishService.updateStatus(status,id);

        //删除所有key为dish_*格式的缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 统一清理redis中的缓存
     * @param pattern 需要清理的redis的key的模式
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
