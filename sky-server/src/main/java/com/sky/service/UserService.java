package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.result.Result;

/**
* @author jiangwb
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2024-02-22 17:59:14
*/
public interface UserService extends IService<User> {

    User wxLogin(UserLoginDTO userLoginDTO);
}
