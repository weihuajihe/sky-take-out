package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.constant.OrderStatus;
import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.DishMapper;

import com.sky.mapper.OrdersMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */



        //查询总订单数
        LambdaQueryWrapper<Orders> timeOrderLambdaQueryWrapper =
                new LambdaQueryWrapper<Orders>().between(Orders::getOrderTime, begin, end);
        Integer totalOrderCount = ordersMapper.selectCount(timeOrderLambdaQueryWrapper).intValue();



        //营业额
        LambdaQueryWrapper<Orders> statusLambdaQueryWrapper = timeOrderLambdaQueryWrapper.eq(Orders::getStatus, OrderStatus.COMPLETED);
        List<Orders> ordersList = ordersMapper.selectList(statusLambdaQueryWrapper);
        double turnover = 0.0;
        BigDecimal money = new BigDecimal(0);
        for (Orders orders : ordersList) {
            money = money.add(orders.getAmount());
        }
        turnover = money.doubleValue();


        //有效订单数
        Integer validOrderCount = (ordersMapper.selectCount(statusLambdaQueryWrapper)).intValue();


        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        LambdaQueryWrapper<User> timeUserLambdaQueryWrapper = new LambdaQueryWrapper<User>().between(User::getCreateTime, begin, end);
        Integer newUsers = userMapper.selectCount(timeUserLambdaQueryWrapper).intValue();

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {


        //今日订单概览
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);

        //待接单
        LambdaQueryWrapper<Orders> waitingLambdaQueryWrapper = new LambdaQueryWrapper<Orders>().gt(Orders::getOrderTime,
                begin).eq(Orders::getStatus, OrderStatus.TO_BE_CONFIRMED);
        Integer waitingOrders = ordersMapper.selectList(waitingLambdaQueryWrapper).size();


        //待派送

        LambdaQueryWrapper<Orders> deliveryLambdaQueryWrapper =
                new LambdaQueryWrapper<Orders>().gt(Orders::getOrderTime,
                        begin).eq(Orders::getStatus,
                OrderStatus.CONFIRMED);
        Integer deliveredOrders = ordersMapper.selectList(deliveryLambdaQueryWrapper).size();


        //已完成
        LambdaQueryWrapper<Orders> completeLambadaQueryWrapper = new LambdaQueryWrapper<Orders>().gt(Orders::getOrderTime,
                begin).eq(Orders::getStatus,
                OrderStatus.COMPLETED);
        Integer completedOrders= ordersMapper.selectList(completeLambadaQueryWrapper).size();


        //已取消
        LambdaQueryWrapper<Orders> cancelLambadaQueryWrapper = new LambdaQueryWrapper<Orders>().gt(Orders::getOrderTime,
                begin).eq(Orders::getStatus,
                OrderStatus.CANCELLED);
        Integer cancelledOrders = ordersMapper.selectList(cancelLambadaQueryWrapper).size();

        //全部订单

        Integer allOrders = ordersMapper.selectList(new LambdaQueryWrapper<Orders>().gt(Orders::getOrderTime,
                begin)).size();

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
