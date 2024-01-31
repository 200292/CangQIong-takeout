package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    /**
     * 新增菜品及其口味
     */
    @Transactional  //涉及两张表的操作，需要使用注解，要么全部成功，要么全部失败
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表添加一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult query(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        List<DishVO> dishes = dishMapper.pageQuery(dishPageQueryDTO);
        PageInfo<DishVO> pageInfo = new PageInfo<>(dishes);
        PageResult pageResult = new PageResult(pageInfo.getTotal(),dishes);
        return pageResult;
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Override
    @Transactional  //涉及多个表的操作
    public void deleteBatch(List<Long> ids) {
        //根据菜品的id操作
        for (Long id : ids) {
            Dish dish = dishMapper.queryById(id);
            //正在售卖中的菜品不能删除
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            int count = setmealDishMapper.querySetmealCountByDishId(id);
            if(count > 0){//说明该菜品至少关联一个套餐
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        //删除菜品,如果菜品关联口味也需要删除
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }

    }


}
