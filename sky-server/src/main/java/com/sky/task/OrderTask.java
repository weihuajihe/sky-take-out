package com.sky.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.OrderStatus;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/11 11:12
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
    处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")//每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单{}", LocalDateTime.now());
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<Orders>().lt(Orders::getOrderTime, LocalDateTime.now().plusMinutes(-15))
                .eq(Orders::getStatus, OrderStatus.PENDING_PAYMENT);
        List<Orders> orders = ordersMapper.selectList(lambdaQueryWrapper);
        if(orders !=null && orders.size()>0){
            orders.forEach(order -> {
                order.setStatus(OrderStatus.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                ordersMapper.updateById(order);
            });
        }

    }

    /**
     * 处理派送中订单的完成
     *
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点触发
    public void processDeliveryOrder(){
        log.info("定时处理派送中订单{}", LocalDateTime.now());
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<Orders>().lt(Orders::getOrderTime,
                        LocalDateTime.now().plusMinutes(-60))
                .eq(Orders::getStatus, OrderStatus.DELIVERY_IN_PROGRESS);
        List<Orders> orders = ordersMapper.selectList(lambdaQueryWrapper);
        if (orders != null && orders.size() > 0) {
            orders.forEach(order -> {
                order.setStatus(OrderStatus.COMPLETED);
                ordersMapper.updateById(order);
            });
        }
    }

}
