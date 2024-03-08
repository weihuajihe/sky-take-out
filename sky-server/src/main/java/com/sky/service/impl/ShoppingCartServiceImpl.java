package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author jiangwb
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2024-02-22 17:59:14
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService {

    //定义购物车初始化数量
    private final Integer ORIGINNUMBER = 1;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public Result addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart item = new ShoppingCart();


        //判断当前要加入购物车的商品是否已经存在
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();


        //判断本次添加的是菜品还是套餐
        if(dishId == null){
            LambdaQueryWrapper<ShoppingCart> setmealLambdaQueryWrapper =
                    new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                            .eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart setmealItem = shoppingCartMapper.selectOne(setmealLambdaQueryWrapper);
            //若存在，则数量加一
            if (null != setmealItem) {
                setmealItem.setNumber(setmealItem.getNumber() + 1);
                shoppingCartMapper.updateById(setmealItem);
                return Result.success();
            }

            //若不存在，则添加
            Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
            item.setAmount(setmeal.getPrice());
            item.setName(setmeal.getName());
            item.setImage(setmeal.getImage());


        }else{
            LambdaQueryWrapper<ShoppingCart> dishLambdaQueryWrapper =
                    new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                            .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                            .eq(ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor());

            ShoppingCart dishItem = shoppingCartMapper.selectOne(dishLambdaQueryWrapper);
            //若存在，则数量加一
            if (null != dishItem) {
                dishItem.setNumber(dishItem.getNumber() + 1);
                shoppingCartMapper.updateById(dishItem);
                return Result.success();
            }

            //若不存在，则添加
            Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
            item.setAmount(dish.getPrice());
            item.setName(dish.getName());
            item.setImage(dish.getImage());

        }


        BeanUtils.copyProperties(shoppingCartDTO, item);
        item.setUserId(BaseContext.getCurrentId());
        item.setNumber(ORIGINNUMBER);
        shoppingCartMapper.insert(item);
        return Result.success();




    }

    @Override
    public Result clear() {
        int delete = shoppingCartMapper.delete(null);
        return Result.success();
    }

    @Override
    public Result del(ShoppingCartDTO shoppingCartDTO) {
        //判断删除的是套餐还是菜品

        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();


        //判断本次添加的是菜品还是套餐
        if (dishId == null) {
            LambdaQueryWrapper<ShoppingCart> setmealLambdaQueryWrapper =
                    new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                            .eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart setmealItem = shoppingCartMapper.selectOne(setmealLambdaQueryWrapper);
            //当前数量为1
            if (setmealItem.getNumber() == ORIGINNUMBER) {
                int rows = shoppingCartMapper.deleteById(setmealItem);
                if(rows>0){
                    return Result.success();
                }
                return Result.error(MessageConstant.UNKNOWN_ERROR);
            }

            //当前数量大于1
            setmealItem.setNumber(setmealItem.getNumber()-1);
            int rows = shoppingCartMapper.updateById(setmealItem);
            if (rows > 0) {
                return Result.success();
            }
            return Result.error(MessageConstant.UNKNOWN_ERROR);


        } else {
            LambdaQueryWrapper<ShoppingCart> dishLambdaQueryWrapper =
                    new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                            .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                            .eq(ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor());

            ShoppingCart dishItem = shoppingCartMapper.selectOne(dishLambdaQueryWrapper);
            //当前数量为1
            if (dishItem.getNumber() == ORIGINNUMBER) {
                int rows = shoppingCartMapper.deleteById(dishItem);

                if (rows > 0) {
                    return Result.success();
                }
                return Result.error(MessageConstant.UNKNOWN_ERROR);
            }

            //当前数量大于1
            dishItem.setNumber(dishItem.getNumber() - 1);
            int rows = shoppingCartMapper.updateById(dishItem);
            if (rows > 0) {
                return Result.success();
            }
            return Result.error(MessageConstant.UNKNOWN_ERROR);

        }






    }
}




