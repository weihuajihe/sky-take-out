package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author jiangwb
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2024-02-22 17:59:13
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    //增加套餐的同时，添加多条setmealDish记录
    @Override
    @Transactional
    public Result saveSetmeal(SetmealDTO setmealDTO) {
        //向套餐表增加一条数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //新增套餐默认起售
        setmeal.setStatus(StatusConstant.ENABLE);
        int rows = setmealMapper.insert(setmeal);
        //向套餐菜品表增加多个数据
        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        boolean flag = false;
        if(setmealDishes != null && setmealDishes.size()>0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(id);
            });
            flag = setmealDishService.saveBatch(setmealDishes);
        }
        if(rows>0&& flag){
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        IPage<SetmealVO> setmealVOPage = new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        IPage page = setmealMapper.pageQuery(setmealVOPage, setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @Override
    @Transactional
    public Result deleteSetmeal(List<Long> ids) {
        //判断菜品是否可以删除
        //1.是否起售

        List<Setmeal> setmeals = setmealMapper.selectBatchIds(ids);
        setmeals.forEach(setmeal -> {
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        //3.正常删除，包括删除套餐菜表中的数据
        int rows = setmealMapper.deleteBatchIds(ids);
        //使用条件构造器针对非主键的集合
        LambdaQueryWrapper<SetmealDish> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        boolean flag = setmealDishService.remove(LambdaQueryWrapper);


        if (rows != 0 && flag) {
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @Override
    public Result<SetmealVO> findById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        List<SetmealDish> setmealDishList =
                setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setCategoryName(categoryMapper.selectById(setmeal.getCategoryId()).getName());
        setmealVO.setSetmealDishes(setmealDishList);
        return Result.success(setmealVO);
    }

    @Override
    public Result updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO, setmeal);
        int rows = setmealMapper.updateById(setmeal);
        //对于口味的改变，需要先删除，再更新！【当数据库无数据时，无法使用sql的update更新】
        //批量删除
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, setmealDTO.getId()));
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishList.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
        boolean flag = setmealDishService.saveBatch(setmealDishList);
        if (flag && rows > 0) {
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @Override
    public Result stopOrStart(Integer status, Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        if(status == StatusConstant.ENABLE) {
            List<SetmealDish> setmealDishList =
                    setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
            setmealDishList.forEach(setmealDish -> {
                if (dishMapper.selectById(setmealDish.getDishId()).getStatus() == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        setmeal.setStatus(status);
        int rows = setmealMapper.updateById(setmeal);
        if (rows > 0) {
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);


    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        ArrayList<DishItemVO> list = new ArrayList<>();
        List<SetmealDish> setmealDishList =
                setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
        setmealDishList.forEach(setmealDish -> {
            DishItemVO dishItemVO = new DishItemVO();
            Dish dish = dishMapper.selectById(setmealDish.getDishId());
            BeanUtils.copyProperties(dish,dishItemVO);
            dishItemVO.setCopies(setmealDish.getCopies());
            list.add(dishItemVO);
        });
        return list;




    }
}




