package com.sky.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("list")
    public Result<List<DishVO>> list(Long categoryId){
        //查询缓存中是否有分类下的菜品数据
        //构造redis中的key
        String key = "dish_"+categoryId;
        //查询redis对应key下的value
        List<DishVO> dishList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //若存在，则不用去数据库查询
        if(dishList != null && dishList.size()>0){
            return Result.success(dishList);
        }


        //若不存在，第一次查询数据库后取出数据放入缓存
        dishList = dishService.listWithFlavor(categoryId);

        redisTemplate.opsForValue().set(key,dishList);
        return Result.success(dishList);
    }
}
