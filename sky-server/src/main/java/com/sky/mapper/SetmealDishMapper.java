package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select count(*) from setmeal_dish where id = #{id}")
    int querySetmealCountByDishId(Long id);

    /**
     * 批量插入套餐中关联的菜品
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id批量删除与它关联的菜品
     * @param ids
     */
    void deleteBatchBySetmealId(List<Long> ids);

    /**
     * 根据套餐id查询关联的菜品
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> queryBySetmealId(Long id);

    /**
     * 根据套餐id删除下属的菜品
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
