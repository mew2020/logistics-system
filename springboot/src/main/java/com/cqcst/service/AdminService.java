package com.cqcst.service;

import com.cqcst.entity.Admin;
import com.cqcst.exception.CustomException;
import com.cqcst.mapper.AdminMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminService {
    //定义一个AdminMapper的私有类,从而去调用该方法
    @Resource
    private AdminMapper adminMapper;
    public Admin login(Admin admin){
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if(dbAdmin == null){
            throw new CustomException("账号或密码错误");
        }
        if(!admin.getPassword().equals(dbAdmin.getPassword())){
            throw new CustomException("账号或密码错误");
        }
        if(dbAdmin.getStatus() < 1) {
            throw new CustomException("账号已被禁用");
        }
        return dbAdmin;
    }

    public void register(Admin admin) {
        adminMapper.insert(admin);
    }

}
