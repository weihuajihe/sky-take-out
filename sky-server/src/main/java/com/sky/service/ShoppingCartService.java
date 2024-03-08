package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;

/**
* @author jiangwb
* @description 针对表【shopping_cart(购物车)】的数据库操作Service
* @createDate 2024-02-22 17:59:14
*/
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加菜品或套餐到购物车
     * @param shoppingCartDTO
     * @return
     */
    Result addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     * @return
     */
    Result clear();

    /**
     * 删除某一个商品
     * @return
     */
    Result del(ShoppingCartDTO shoppingCartDTO);
}
