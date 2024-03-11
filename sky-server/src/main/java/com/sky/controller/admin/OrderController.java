package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.constant.OrderStatus;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/10 17:12
 */
@RestController("adminOrderController")
@RequestMapping("admin/order")
@Api(tags = "管理端订单模块")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = ordersService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("statistics")
    @ApiOperation("统计订单数量")
    public Result<OrderStatisticsVO> statistics(){
        Result r = ordersService.statistic();
        return r;
    }

    @GetMapping("details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> detail(@PathVariable Long id){
        Result r = ordersService.detail(id);
        return r;
    }

    @PutMapping("delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        Result r = ordersService.delivery(id);
        return r;
    }
    @PutMapping("complete/{id}")
    @ApiOperation("派送订单")
    public Result complete(@PathVariable Long id){
        Result r = ordersService.complete(id);
        return r;
    }

    @PutMapping("confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        Orders orders = ordersService.getById(ordersConfirmDTO.getId());
        orders.setStatus(OrderStatus.CONFIRMED);
        boolean flag = ordersService.updateById(orders);
        if(flag){
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @PutMapping("rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        Result r = ordersService.reject(ordersRejectionDTO);
        return r;
    }

    @PutMapping("cancel")
    @ApiOperation("取消订单")
    public Result adminCancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        Result r = ordersService.adminCancel(ordersCancelDTO);
        return r;
    }


}
