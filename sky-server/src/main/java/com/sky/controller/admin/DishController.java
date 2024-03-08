package com.sky.controller.admin;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;

import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：菜品管理
 * @projectName sky-take-out
 * @date 2024/2/23 21:59
 */
@RestController("adminDishController")
@Slf4j
@RequestMapping("admin/dish")
@Api(tags = "菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        Result r = dishService.insertDishWithFlavor(dishDTO);
        clearRedis("dish_"+dishDTO.getCategoryId());
        return r;
    }
    @GetMapping("page")
    @ApiOperation("菜品分页查询")
    public Result page(DishPageQueryDTO dishPageQueryDTO){
        Result<PageResult> r = dishService.pageQuery(dishPageQueryDTO);
        return r;
    }



    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        Result r = dishService.deleteDish(ids);
        clearRedis("dish_*");
        return r;
    }

    //修改前需要根据id回显数据
    @GetMapping("{id}")
    @ApiOperation("根据菜品id查询菜品")
    public Result<DishVO> findById(@PathVariable Long id) {
        Result<DishVO> r = dishService.findById(id);
        return r;
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        Result r = dishService.updateDish(dishDTO);
        clearRedis("dish_*");
        return r;
    }

    @PostMapping("status/{status}")
    @ApiOperation("修改停售/起售状态")
    public Result stopOrStart( @PathVariable Integer status, Long id){
        Result r = dishService.stopOrStart(status,id);
        clearRedis("dish_*");
        return r;
    }

    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> selectById(Long categoryId,String name){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(name == null){
            dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);
        }else{
            dishLambdaQueryWrapper.like(!StringUtils.isEmpty(name), Dish::getName, name);
        }
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        return Result.success(list);

    }

    private void clearRedis(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
