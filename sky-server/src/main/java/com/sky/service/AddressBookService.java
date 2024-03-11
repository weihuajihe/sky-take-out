package com.sky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.entity.AddressBook;

import java.util.List;

/**
* @author jiangwb
* @description 针对表【address_book(地址簿)】的数据库操作Service
* @createDate 2024-02-22 17:59:13
*/
public interface AddressBookService extends IService<AddressBook> {

    List<AddressBook> list(AddressBook addressBook);

    void saveAdd(AddressBook addressBook);

    AddressBook getById(Long id);

    void update(AddressBook addressBook);

    void setDefault(AddressBook addressBook);

    void deleteById(Long id);
}
