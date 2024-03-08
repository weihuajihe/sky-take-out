package com.sky.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/2/28 23:54
 */
@RestController("userSetmealController")
@RequestMapping("user/setmeal")
@Api(tags = "c端-套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    //根据分类id查询套餐
    @GetMapping("list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache" ,key = "#categoryId") // key: setmealCache::categoryId
    public Result<List<Setmeal>> list(Long categoryId){
        List<Setmeal> setmealList = setmealService.list(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, categoryId)
                .eq(Setmeal::getStatus, StatusConstant.ENABLE));
        return Result.success(setmealList);

    }

    @GetMapping("dish/{id}")
    @ApiOperation("获取套餐菜品")
    public Result<List<DishItemVO>> dishItem(@PathVariable Long id){
        List<DishItemVO> dishItemVOList = setmealService.getDishItemById(id);
        return Result.success(dishItemVOList);
    }
}
