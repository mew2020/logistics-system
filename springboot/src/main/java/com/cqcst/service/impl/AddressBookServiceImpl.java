package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.entity.AddressBook;
import com.cqcst.service.AddressBookService;
import com.cqcst.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【address_book】的数据库操作Service实现
* @createDate 2024-03-27 23:17:08
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{
    @Override
    public List<AddressBook> getAddressBook(Integer uid) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,uid)
                .orderByDesc(AddressBook::getTag).orderByDesc(AddressBook::getId); //tag优先，其他按插入时间倒序返回
        return this.list(queryWrapper);
    }

    @Override
    public List<AddressBook> getAddressBookByCondition(Integer uid, String str) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, uid)
                .like(AddressBook::getName,str)
                .or().like(AddressBook::getPhone,str)
                .or().like(AddressBook::getLocation,str)
                .orderByDesc(AddressBook::getTag).orderByDesc(AddressBook::getId); //tag优先，其他按插入时间倒序返回
        return this.list(queryWrapper);
    }

    @Override
    public AddressBook getLastByUserId(Integer uid) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,uid).orderByDesc(AddressBook::getId);
        queryWrapper.last("limit 1");
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean updateAddressBook(AddressBook addressBook) {
        return updateById(addressBook);
    }

    @Override
    public boolean deleteAddressBook(Integer id) {
        return removeById(id);
    }
}


