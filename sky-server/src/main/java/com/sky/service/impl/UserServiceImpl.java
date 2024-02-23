package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author jiangwb
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2024-02-22 17:59:14
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




