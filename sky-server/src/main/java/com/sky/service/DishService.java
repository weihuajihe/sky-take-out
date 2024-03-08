package com.sky.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;

import java.util.List;

/**
* @author jiangwb
* @description 针对表【dish(菜品)】的数据库操作Service
* @createDate 2024-02-22 17:59:13
*/
public interface DishService extends IService<Dish> {

    /**
     * 插入带口味的菜品
     * @param dishDTO
     * @return
     */
    Result insertDishWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 可以批量删除，也可以单独删除
     * 起售的菜品不可以删除
     * 被套餐关联的菜品不可以删除
     * 删除菜品后，关联口味也删除
     * @param ids
     * @return
     */
    Result deleteDish(List<Long> ids);

    /**
     *根据id查询菜品
     * @param id
     * @return
     */
    Result<DishVO> findById(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    Result updateDish(DishDTO dishDTO);

    /**
     * 修改菜品的销售状态
     * @param status
     * @return
     */
    Result stopOrStart( Integer status,Long id);

    /**
     * 查询带口味的菜品，并放入缓存
     * @param categoryId
     * @return
     */
    List<DishVO> listWithFlavor(Long categoryId);
}
