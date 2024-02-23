package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiangwb
* @description 针对表【address_book(地址簿)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




