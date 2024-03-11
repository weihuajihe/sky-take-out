package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

/**
* @author jiangwb
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2024-02-22 17:59:13
*/
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    Result<OrderSubmitVO> submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    void paySuccess(String outTradeNo);

    /**
     * 分页查询历史订单
     * @param page
     * @param pageSize
     * @return
     */
    PageResult pageOrders(Integer page, Integer pageSize, Integer status);

    /**
     * 查看订单详情
      * @param id
     * @return
     */
    Result orderDetail(Long id);

    /**
     * 取消订单
     * @param id
     * @return
     */
    Result cancel(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     * @return
     */
    Result repetition(Long id);

    /**
     * 催单
     * @param id
     * @return
     */
    Result reminder(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 不同状态订单的统计
     * @return
     */
    Result statistic();

    /**
     * 查看订单详情
     * @return
     */
    Result detail( Long id);

    /**
     * 拒单
     * @param ordersRejectionDTO
     * @return
     */
    Result reject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 管理端取消订单
     * @param ordersCancelDTO
     * @return
     */
    Result adminCancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     * @return
     */
    Result delivery(Long id);

    Result complete(Long id);
}
