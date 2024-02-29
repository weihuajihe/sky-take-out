package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;

import java.util.List;

/**
* @author jiangwb
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2024-02-22 17:59:13
*/
public interface CategoryService extends IService<Category> {
    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     *
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    List<Category> listCategory(Integer type);
}



