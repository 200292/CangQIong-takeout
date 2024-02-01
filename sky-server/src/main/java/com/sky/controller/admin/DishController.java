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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/dish")
@Slf4j
/**
 * 菜品管理
 */
public class DishController {
    @Autowired
    private DishService dishService;
    @PostMapping()
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品 {}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
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
        return Result.success();
    }

}
