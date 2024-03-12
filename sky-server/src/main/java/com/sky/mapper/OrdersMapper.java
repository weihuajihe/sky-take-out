package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
* @author jiangwb
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {


    /**
     * 销售排行top10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSaleTop(LocalDateTime begin,LocalDateTime end);


}




