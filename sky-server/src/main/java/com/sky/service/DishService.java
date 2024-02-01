package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品及其口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult query(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据菜品id查询菜品,包含口味信息
     * @param id
     * @return
     */
    DishVO getWithFlavorById(Long id);

    /**
     * 修改菜品及其口味
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 起售停售套餐
     * @param status
     */
    void updateStatus(int status,Long id);
}
