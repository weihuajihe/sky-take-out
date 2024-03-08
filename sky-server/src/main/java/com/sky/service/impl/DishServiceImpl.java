package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
* @author jiangwb
* @description 针对表【dish(菜品)】的数据库操作Service实现
* @createDate 2024-02-22 17:59:13
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;




    @Override
    //涉及两张表的操作，增加事务管理，保证操作的原子性
    @Transactional
    public Result insertDishWithFlavor(DishDTO dishDTO) {
        //向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        int rows = dishMapper.insert(dish);
        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        boolean flag = false;
        if(flavors != null && flavors.size()>0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            flag = dishFlavorService.saveBatch(flavors);
        }
        if(rows > 0 && flag){
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);


    }

    @Override
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        IPage<DishVO> dishVOPage = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        IPage page = dishMapper.pageQuery(dishVOPage,dishPageQueryDTO);
        return Result.success(new PageResult(page.getTotal(),page.getRecords()));
    }

    //多表操作，加上事务注解
    @Override
    @Transactional
    public Result deleteDish(List<Long> ids) {
        //判断菜品是否可以删除
        //1.是否起售

        List<Dish> dishes = dishMapper.selectBatchIds(ids);
        dishes.forEach(dish -> {
            if(dish.getStatus()== StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        //2.查询套餐中是否包含该菜品
        LambdaQueryWrapper<SetmealDish> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(setmealLambdaQueryWrapper);
        if(setmealDishes != null & setmealDishes.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3.正常删除，包括删除口味
        int rows = dishMapper.deleteBatchIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        boolean flag = dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        if(rows!=0 && flag){
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);


    }

    @Override
    public Result<DishVO> findById(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();
        List<DishFlavor> dishFavorList =
                dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setCategoryName(categoryMapper.selectById(dish.getCategoryId()).getName());
        dishVO.setFlavors(dishFavorList);
        return Result.success(dishVO);
    }

    @Override
    @Transactional
    public Result updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);
        int rows = dishMapper.updateById(dish);
        //对于口味的改变，需要先删除，再更新！【当数据库无数据时，无法更新】
        //批量删除
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,dishDTO.getId()));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(dishFlavor -> dishFlavor.setDishId( dishDTO.getId()));
        boolean flag = dishFlavorService.saveBatch(flavors);

        return Result.success();


    }

    @Override
    public Result stopOrStart(Integer status,Long id) {
        Dish dish = dishMapper.selectById(id);
        dish.setStatus(status);
        int rows = dishMapper.updateById(dish);

        return Result.success();

    }

    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        //仅查询起售中的商品
        ArrayList<DishVO> dishVOArrayList = new ArrayList<>();
        List<Dish> dishList = dishMapper.selectList(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus,StatusConstant.ENABLE));
        dishList.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish,dishVO);
            List<DishFlavor> flavorList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId()));
            dishVO.setFlavors(flavorList);
            dishVOArrayList.add(dishVO);
        });


        return dishVOArrayList;

    }
}




