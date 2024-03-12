package com.sky.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.constant.*;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderDetailService;
import com.sky.service.OrdersService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author jiangwb
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2024-02-22 17:59:13
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService {

    @Value("${sky.shop.address}")
    private String shopAddress;

    @Value("${sky.baidu.ak}")
    private String ak;

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private WebSocketServer webSocketServer;


    @Transactional
    public Result<OrderSubmitVO> submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常
        //1.地址为空

        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            throw  new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        checkOutOfRange(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());


        //2.购物车数据为空
        List<ShoppingCart> shoppingCartList =
                shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,
                BaseContext.getCurrentId()));
        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }




        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);

        orders.setPayStatus(PayStatus.UN_PAID);
        orders.setStatus(OrderStatus.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setOrderTime(LocalDateTime.now());
        orders.setUserId(BaseContext.getCurrentId());

        orders.setAddress(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());


        int orderRows = ordersMapper.insert(orders);


        //向订单明细表插入多条数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        shoppingCartList.forEach(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetail.setId(null);
            orderDetails.add(orderDetail);
        });
        boolean flag = orderDetailService.saveBatch(orderDetails);

        //下单成功(且订单已支付)，清空用户的购物车数据
        if(orderRows>0 && flag && orders.getStatus() > OrderStatus.PENDING_PAYMENT){
            shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,
                    BaseContext.getCurrentId()));
        }


        //封装VO返回结果
        return Result.success(OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build());
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);

        //调用微信支付接口，生成预支付交易单
        //JSONObject jsonObject = weChatPayUtil.pay(
        //        ordersPaymentDTO.getOrderNumber(), //商户订单号
        //        new BigDecimal(0.01), //支付金额，单位 元
        //        "苍穹外卖订单", //商品描述
        //        user.getOpenid() //微信用户的openid
        //);
        //
        //if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
        //    throw new OrderBusinessException("该订单已支付");
        //}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>().eq(Orders::getNumber, outTradeNo));

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(OrderStatus.TO_BE_CONFIRMED)
                .payStatus(PayStatus.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.updateById(orders);

        //通过webSocket推送消息
        //JSNO格式：type,orderId,content

        Map data = new HashMap<>();
        data.put("type", MessageType.ORDER_REMIND);
        data.put("orderId",ordersDB.getId());

        data.put("content","订单号"+outTradeNo);
        String s = JSON.toJSONString(data);
        webSocketServer.sendToAllClient(s);
    }

    @Override
    public PageResult pageOrders(Integer page, Integer pageSize, Integer status) {
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null != (ordersPageQueryDTO.getStatus()), Orders::getStatus,
                ordersPageQueryDTO.getStatus())
                .eq(Orders::getUserId,ordersPageQueryDTO.getUserId());
        Page<Orders> queryPage = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = ordersMapper.selectPage(queryPage, lambdaQueryWrapper);
        long total = ordersPage.getTotal();
        List<Orders> records = ordersPage.getRecords();
        List<OrderVO> recordVOList = new ArrayList<>();


        if(ordersPage!= null && total>0){
            records.forEach(record->{
                List<OrderDetail> orderDetails = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId,
                        record.getId()));
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(record,orderVO);
                orderVO.setOrderDetailList(orderDetails);
                recordVOList.add(orderVO);
            });
        }
        PageResult pageResult = new PageResult(total, recordVOList);
        return pageResult;
    }

    @Override
    public Result orderDetail(Long id) {
        OrderVO orderVO = new OrderVO();
        Orders orders = ordersMapper.selectById(id);
        BeanUtils.copyProperties(orders,orderVO);
        List<OrderDetail> list = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, id));
        orderVO.setOrderDetailList(list);
        return Result.success(orderVO);
    }

    @Override
    public Result cancel(Long id) throws Exception{
        Orders order = ordersMapper.selectById(id);

        //检查订单是否存在
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //仅待接单和待支付状态下可取消订单
        if(order.getStatus() > OrderStatus.CANCELABLE){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //退款
        if(order.getStatus() == OrderStatus.TO_BE_CONFIRMED){
            order.setPayStatus(PayStatus.REFUND);
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason("用户取消");
        order.setCancelTime(LocalDateTime.now());
        ordersMapper.updateById(order);
        return Result.success();
    }

    @Override
    public Result repetition(Long id) {



        List<OrderDetail> orderDetailList =
                orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, id));;
        ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCarts.add(shoppingCart);

        });
        boolean flag = shoppingCartService.saveBatch(shoppingCarts);
        if(flag){
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);


    }

    @Override
    public Result reminder(Long id) {

        Orders ordersDB = ordersMapper.selectById(id);
        if(ordersDB.getStatus() > OrderStatus.REMINDERABLE){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //通过webSocket推送消息
        //JSNO格式：type,orderId,content

        Map data = new HashMap<>();
        data.put("type", MessageType.CUSTOMER_REMIND);
        data.put("orderId", ordersDB.getId());

        data.put("content", "订单号" + ordersDB.getNumber());
        String s = JSON.toJSONString(data);
        webSocketServer.sendToAllClient(s);
        return Result.success();
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>().like(null != ordersPageQueryDTO.getNumber(), Orders::getNumber,
                        ordersPageQueryDTO.getNumber())
                .like(null != ordersPageQueryDTO.getPhone(), Orders::getPhone,
                        ordersPageQueryDTO.getPhone())
                .eq(null != ordersPageQueryDTO.getStatus(),Orders::getStatus,ordersPageQueryDTO.getStatus())
                .between(null != ordersPageQueryDTO.getBeginTime() && null != ordersPageQueryDTO.getEndTime(),
                        Orders::getOrderTime, ordersPageQueryDTO.getBeginTime(), ordersPageQueryDTO.getEndTime())
                .orderByDesc(Orders::getOrderTime);

        Page<Orders> page = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = ordersMapper.selectPage(page, queryWrapper);

        long total = ordersPage.getTotal();
        List<Orders> records = ordersPage.getRecords();


        List<OrderVO> recordVOList = new ArrayList<>();


        if (ordersPage != null && total > 0) {
            records.forEach(record -> {

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(record, orderVO);
                String orderDishes = getOrderDishesStr(record);
                orderVO.setOrderDishes(orderDishes);

                recordVOList.add(orderVO);
            });
        }
        PageResult pageResult = new PageResult(total, recordVOList);
        return pageResult;

    }

    @Override
    public Result statistic() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        List<Orders> toBeConfirmed = ordersMapper.selectList(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus,
                OrderStatus.TO_BE_CONFIRMED));
        List<Orders> confirmed = ordersMapper.selectList(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus,
                OrderStatus.CONFIRMED));
        List<Orders> deliveryInProgress = ordersMapper.selectList(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus,
                OrderStatus.DELIVERY_IN_PROGRESS));
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed.size());
        orderStatisticsVO.setConfirmed(confirmed.size());
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress.size());
        return Result.success(orderStatisticsVO);
    }

    @Override
    public Result detail(Long id) {
        OrderVO orderVO = new OrderVO();

        Orders orders = ordersMapper.selectById(id);
        BeanUtils.copyProperties(orders,orderVO);
        String orderDishesStr = getOrderDishesStr(orders);
        orderVO.setOrderDishes(orderDishesStr);
        List<OrderDetail> orderDetailList =
                orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, id));

        orderVO.setOrderDetailList(orderDetailList);
        return Result.success(orderVO);
    }

    @Override
    public Result reject(OrdersRejectionDTO ordersRejectionDTO) {
        //仅订单是待接单才能取消
        Orders orders = ordersMapper.selectById(ordersRejectionDTO.getId());

        if(orders == null || orders.getStatus() != OrderStatus.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //若已支付则需退款
        if(orders.getPayStatus() == PayStatus.PAID){
            orders.setPayStatus(PayStatus.REFUND);
        }
        orders.setCancelReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(OrderStatus.CANCELLED);
        int rows = ordersMapper.updateById(orders);

        if(rows>0){
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);


    }

    @Override
    public Result adminCancel(OrdersCancelDTO ordersCancelDTO) {
        //仅订单是待接单才能取消
        Orders orders = ordersMapper.selectById(ordersCancelDTO.getId());



        //若已支付则需退款
        if (orders.getPayStatus() == PayStatus.PAID) {
            orders.setPayStatus(PayStatus.REFUND);
        }
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(OrderStatus.CANCELLED);
        int rows = ordersMapper.updateById(orders);

        if (rows > 0) {
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @Override
    public Result delivery(Long id) {
        //仅订单是待派送才能取消
        Orders orders = ordersMapper.selectById(id);

        if (orders == null || orders.getStatus() != OrderStatus.CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }




        orders.setStatus(OrderStatus.DELIVERY_IN_PROGRESS);
        int rows = ordersMapper.updateById(orders);

        if (rows > 0) {
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    @Override
    public Result complete(Long id) {
        //仅订单是派送中才能取消
        Orders orders = ordersMapper.selectById(id);

        if (orders == null || orders.getStatus() != OrderStatus.DELIVERY_IN_PROGRESS) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


        orders.setStatus(OrderStatus.COMPLETED);
        int rows = ordersMapper.updateById(orders);

        if (rows > 0) {
            return Result.success();
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList =
           orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orders.getId()));

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 检查客户的收货地址是否超出配送范围
     *
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address", address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin", shopLngLat);
        map.put("destination", userLngLat);
        map.put("steps_info", "0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if (distance > 5000) {
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }
}




