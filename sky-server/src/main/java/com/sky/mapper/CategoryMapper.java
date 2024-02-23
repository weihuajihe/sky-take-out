package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




