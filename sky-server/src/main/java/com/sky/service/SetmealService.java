package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
* @author jiangwb
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2024-02-22 17:59:14
*/
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐业务接口
     * @param setmealDTO
     * @return
     */
    Result saveSetmeal(SetmealDTO setmealDTO);

    /**
     * 根据套餐名称，套餐分类，套餐状态分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 可以批量删除，也可以单独删除
     * 起售的套餐不可以删除
     * 删除套餐后，套餐菜表中的数据也删除
     *
     * @param ids
     * @return
     */
    Result deleteSetmeal(List<Long> ids);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    Result<SetmealVO> findById(Long id);

    /**
     * 修改套餐包括修改套餐菜表的数据
     *
     * @param setmealDTO
     * @return
     */
    Result updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 修改套餐的销售状态
     * 含有未起售菜品的套餐无法启售
     *
     * @param status
     * @return
     */
    Result stopOrStart(Integer status, Long id);

    /**
     * 用户端查看套餐中菜品详情
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
