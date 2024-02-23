package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【dish(菜品)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}




