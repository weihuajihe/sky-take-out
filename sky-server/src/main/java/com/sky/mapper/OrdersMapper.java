package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




