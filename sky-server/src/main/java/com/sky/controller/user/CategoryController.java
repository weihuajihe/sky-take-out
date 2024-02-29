package com.sky.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/2/28 22:28
 */
@RestController("userCategoryController")
@Slf4j
@Api(tags = "c端查看分类相关接口")
@RequestMapping("user/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    @ApiOperation("查询分类")
    public Result<List<Category>> list(Integer type){
        if(type == null){
            List<Category> list = categoryService.list();
            return Result.success(list);
        }else{
            List<Category> categoryList = categoryService.listCategory(type);
            return Result.success(categoryList);
        }

    }
}
