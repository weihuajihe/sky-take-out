package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author jiangwb
* @description 针对表【dish(菜品)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    IPage pageQuery(IPage<DishVO> dishVOPage, @Param("dishPageQueryDTO")DishPageQueryDTO dishPageQueryDTO);
}




