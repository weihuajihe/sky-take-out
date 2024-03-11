package com.sky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author jiangwb
* @description 针对表【address_book(地址簿)】的数据库操作Mapper
* @createDate 2024-02-22 17:59:13
* @Entity generator.domain.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
    /**
     * 条件查询
     *
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增
     *
     * @param addressBook
     */
    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insertAdd(AddressBook addressBook);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 根据id修改
     *
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据 用户id修改 是否默认地址
     *
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     *
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);
}




