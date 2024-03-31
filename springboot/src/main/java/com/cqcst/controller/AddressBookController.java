package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.AddressBook;
import com.cqcst.service.AddressBookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressBookController {

    @Resource
    AddressBookService abService;

    @GetMapping
    public Result getAddressBook(@RequestParam Integer uid) {
        List<AddressBook> list = abService.getAddressBook(uid);
        return Result.success(list);
    }

    @GetMapping("/condition/{uid}")
    public Result getAddressBookByUidAndCondition(@PathVariable Integer uid, @RequestParam String condition) {
        List<AddressBook> list = abService.getAddressBookByCondition(uid, condition);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result getAddressBookById(@PathVariable Integer id) {
        AddressBook addr = abService.getById(id);
        return addr == null ? Result.error() : Result.success(addr);
    }

    @GetMapping("/last/{uid}")
    public Result getLastAddressBookByUserId(@PathVariable Integer uid) {
        AddressBook addr = abService.getLastByUserId(uid);
        return addr == null ? Result.error() : Result.success(addr);
    }

    @PutMapping
    public Result updateAddressBook(@RequestBody AddressBook addressBook) {
        System.out.println(addressBook);
        return abService.updateAddressBook(addressBook) ? Result.success() : Result.error();
    }

    @PostMapping
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        addressBook.setId(null); //手动设置一下 防止插入主键冲突
        return abService.save(addressBook) == true ? Result.success() : Result.error();
    }

    @DeleteMapping("/{id}")
    public Result deleteAddressBook(@PathVariable Integer id) {
        System.out.println(id);
        return abService.deleteAddressBook(id) ? Result.success() : Result.error();
    }
}
