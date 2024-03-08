package com.sky.controller.user;

import com.google.j2objc.annotations.AutoreleasePool;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/8 16:50
 */
@RestController
@Slf4j
@Api(tags = "购物车模块")
@RequestMapping("user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("add")
    @ApiOperation("添加到购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        Result r = shoppingCartService.addShoppingCart(shoppingCartDTO);
        return r;
    }

    @GetMapping("list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @DeleteMapping("clean")
    @ApiOperation("清空购物车")
    public Result clean(){
        Result r = shoppingCartService.clear();
        return r;
    }

    @PostMapping("sub")
    @ApiOperation("删除某一个商品")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        Result r = shoppingCartService.del(shoppingCartDTO);
        return r;
    }
}
