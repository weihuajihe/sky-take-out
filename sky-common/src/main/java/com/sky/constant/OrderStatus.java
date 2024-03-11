package com.sky.constant;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/3/8 23:39
 */
public class OrderStatus {
    //待付款
    public static final Integer PENDING_PAYMENT = 1;


    //待接单
    public static final Integer TO_BE_CONFIRMED = 2;
    //已接单(待派送)
    public static final Integer CONFIRMED = 3;
    //派送中
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    //已完成
    public static final Integer COMPLETED = 5;
    //已取消
    public static final Integer CANCELLED = 6;
    //可取消的阈值
    public static final Integer CANCELABLE = 2;
    //可催单阈值
    public static final Integer REMINDERABLE = 3;


}
