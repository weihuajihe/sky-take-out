package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author jiangwb
* @description 针对表【setmeal(套餐)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.Setmeal
*/
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    IPage pageQuery(IPage<SetmealVO> setmealVOPage,@Param("setmealPageQueryDTO") SetmealPageQueryDTO setmealPageQueryDTO);
}




