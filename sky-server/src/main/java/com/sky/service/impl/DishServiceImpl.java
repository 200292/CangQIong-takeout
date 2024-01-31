package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    /**
     * 新增菜品及其口味
     */
    @Transactional  //涉及两张表的操作，需要使用注解，要么全部成功，要么全部失败
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表添加一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //dish.setStatus(StatusConstant.ENABLE);
        dishMapper.insert(dish);
        //通过返回主键值的设置获取insert后dishId
        Long id = dish.getId();
        //向口味表添加n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //遍历设置口味的dishId
            for (DishFlavor flavor :flavors) {
                flavor.setDishId(id);
            }
            dishFlavorMapper.insertBetch(flavors);
        }
    }
}
