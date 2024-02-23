package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:14
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




