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
            dishFlavorMapper.insertBatch(flavors);
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
            Dish dish = dishMapper.queryByDishId(id);
            //正在售卖中的菜品不能删除
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            //得到菜品-套餐表中该菜品对应的套餐数目
            int count = setmealDishMapper.querySetmealCountByDishId(id);
            if(count > 0){//说明该菜品至少关联一个套餐
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        //删除菜品,如果菜品关联口味也需要删除
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //批量删除菜品和菜品对应的口味
        dishMapper.deleteBatchById(ids);
        dishFlavorMapper.deleteBatchByDishId(ids);
    }

    /**
     * 根据菜品id查询菜品,包含口味信息
     * @param id
     * @return
     */
    @Override
    public DishVO getWithFlavorById(Long id) {
        //先查询菜品的基本属性，然后获取菜品对应的口味，封装到一个DishVO对象中
        Dish dish = dishMapper.queryByDishId(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品及其口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //删除该菜品全部的口味，然后将修改后的口味新增
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //遍历设置口味的dishId
            for (DishFlavor flavor :flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.queryByCategoryId(categoryId);
        return dishes;
    }

    /**
     * 起售停售套餐
     * @param status
     */
    @Override
    public void updateStatus(int status,Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }
}
