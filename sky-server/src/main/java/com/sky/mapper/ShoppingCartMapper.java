package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:14
* @Entity generator.domain.ShoppingCart
*/
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}




