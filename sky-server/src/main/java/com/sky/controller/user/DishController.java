package com.sky.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/2/28 23:14
 */
@RestController("userDishController")
@RequestMapping("user/dish")
@Api(tags = "c端-菜品浏览窗口")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("list")
    public Result<List<DishVO>> list(Long categoryId){
        List<DishVO> dishList = dishService.listWithFlavor(categoryId);
        return Result.success(dishList);
    }
}
