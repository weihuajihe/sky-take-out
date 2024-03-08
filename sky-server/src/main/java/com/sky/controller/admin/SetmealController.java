package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/2/25 16:23
 */
@RestController("adminSetmealController")
@Slf4j
@Api(tags = "套餐管理")
@RequestMapping("admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐业务")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        Result r = setmealService.saveSetmeal(setmealDTO);
        return r;
    }

    @GetMapping("page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pr = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pr);
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteDish(@RequestParam List<Long> ids) {
        Result r = setmealService.deleteSetmeal(ids);
        return r;
    }

    //修改前需要根据id回显数据
    @GetMapping("{id}")
    @ApiOperation("根据套餐id查询菜品")
    public Result<SetmealVO> findById(@PathVariable Long id) {
        Result<SetmealVO> r = setmealService.findById(id);
        return r;
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result updateDish(@RequestBody SetmealDTO setmealDTO) {
        Result r = setmealService.updateSetmeal(setmealDTO);
        return r;
    }

    @PostMapping("status/{status}")
    @ApiOperation("修改停售/起售状态")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result stopOrStart(@PathVariable Integer status, Long id) {
        Result r = setmealService.stopOrStart(status, id);
        return r;
    }
}
