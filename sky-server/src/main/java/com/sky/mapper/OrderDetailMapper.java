package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.OrderDetail
*/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




