package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.Admin;
import com.cqcst.service.AdminService;
import com.cqcst.util.AdminContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class WebController {
    //调用service方法
    @Resource
    private AdminService adminService;

    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }
    @PostMapping("/login")
    public Result login(@RequestBody Admin admin){
        Admin dbAdmin = adminService.login(admin);
        if(dbAdmin != null) {
            AdminContext.setAdmin(dbAdmin);
        }
        return Result.success(dbAdmin);
    }
    @PostMapping("/register")
    public Result register(@RequestBody Admin admin){
        adminService.register(admin);
        return Result.success();
    }
}
