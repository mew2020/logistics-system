package com.cqcst.service;

import com.cqcst.entity.AddressBook;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【address_book】的数据库操作Service
* @createDate 2024-03-27 23:17:08
*/
public interface AddressBookService extends IService<AddressBook> {

    List<AddressBook> getAddressBook(Integer uid);

    boolean updateAddressBook(AddressBook addressBook);

    boolean deleteAddressBook(Integer id);

    List<AddressBook> getAddressBookByCondition(Integer uid, String str);

    AddressBook getLastByUserId(Integer uid);
}
