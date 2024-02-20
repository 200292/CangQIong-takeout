package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增菜品
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.id")//新增一个套餐时，该分类下会多一个套餐，需要清除缓存
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐 {}",setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询 {}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//不方便精确清除缓存
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除 {}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据套餐id获取套餐
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("获取套餐 {}",id);
        SetmealVO setmealVO = setmealService.getWithDishById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    /**
     * 修改套餐及套餐内的菜品
     */
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//可能会修改套餐的分类，影响两条缓存，因此全部清除
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐 {}",setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 起售停售套餐
     * @param status
     * @return
     */
    @PostMapping("status/{status}")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//通过查询才能获知套餐的分类，则直接全部清除
    public Result updateStatus(@PathVariable int status,Long id){
        log.info("修改起售停售信息,状态:{} id:{}",status,id);
        setmealService.updateStatus(status,id);
        return Result.success();
    }
}
