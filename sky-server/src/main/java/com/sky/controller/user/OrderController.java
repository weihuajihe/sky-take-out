package com.sky.controller.user;

import com.sky.constant.OrderStatus;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/8 23:15
 */
@RestController("userOrderController")
@Slf4j
@Api(tags = "用户端订单相关接口")
@RequestMapping("user/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit( @RequestBody OrdersSubmitDTO ordersSubmitDTO){

        Result<OrderSubmitVO> r = ordersService.submitOrder(ordersSubmitDTO);
        return r;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("historyOrders")
    @ApiOperation("查看历史订单")
    public Result<PageResult> pageHistoryOrders(Integer page, Integer pageSize, Integer status){
        PageResult pageResult = ordersService.pageOrders(page,pageSize,status);
        return Result.success(pageResult);
    }

    @GetMapping("orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result orderDetail(@PathVariable Long id){
        Result r = ordersService.orderDetail(id);
        return r;
    }

    @PutMapping("cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) throws Exception {
        Result r = ordersService.cancel(id);
        return r;
    }

    @PostMapping("repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        Result r = ordersService.repetition(id);
        return r;

    }

    @GetMapping("reminder/{id}")
    @ApiOperation("催单")
    public Result reminder(@PathVariable Long id){
        Result r = ordersService.reminder(id);
        return r;
    }
}
