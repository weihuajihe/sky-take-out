package com.sky.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author 江武兵
 * @version 1.0
 * @description ：
 * @projectName sky-take-out
 * @date 2024/2/23 16:53
 */
@Configuration
@Slf4j
public class AutoFillConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("员工模块发生插入操作...");
        this.setFieldValByName(AutoFillConstant.CREATE_USER_FIELD,BaseContext.getCurrentId(),metaObject);
        this.setFieldValByName(AutoFillConstant.UPDATE_USER_FIELD,BaseContext.getCurrentId(),metaObject);
        this.setFieldValByName(AutoFillConstant.CREATE_TIME_FIELD, LocalDateTime.now(),metaObject);
        this.setFieldValByName(AutoFillConstant.UPDATE_TIME_FIELD, LocalDateTime.now(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("员工模块发生修改操作...");
        this.setFieldValByName(AutoFillConstant.UPDATE_USER_FIELD, BaseContext.getCurrentId(), metaObject);
        this.setFieldValByName(AutoFillConstant.UPDATE_TIME_FIELD, LocalDateTime.now(), metaObject);


    }
}
