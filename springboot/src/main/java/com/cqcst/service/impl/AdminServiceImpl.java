package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.common.Result;
import com.cqcst.entity.Admin;
import com.cqcst.entity.Site;
import com.cqcst.exception.CustomException;
import com.cqcst.mapper.AdminMapper;
import com.cqcst.service.AdminService;
import com.cqcst.service.SiteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    SiteService siteService;

    public Admin login(Admin admin){
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, admin.getUsername()).eq(Admin::getPassword, admin.getPassword());
        Admin dbAdmin = getOne(queryWrapper);
        if(dbAdmin == null){
            throw new CustomException("账号或密码错误");
        }
        if(dbAdmin.getStatus() < 1) {
            throw new CustomException("账号已被禁用");
        }
        dbAdmin.setPassword(null);
        return dbAdmin;
    }


    public void register(Admin admin) {
        save(admin);
    }


    public List<Admin> getAdminList(String condition, Integer status) {
        if((condition == null || condition.equals("")) && status == null) {
            return list().stream().map(admin -> {
                admin.setPassword(null);
                return admin;
            }).collect(Collectors.toList());
        }

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if(status != null) {
            queryWrapper.eq(Admin::getStatus, status);
        }
        if(condition != null && !condition.equals("")) {
            queryWrapper.like(Admin::getUsername, condition)
                    .or().like(Admin::getName, condition)
                    .or().like(Admin::getPhone, condition);
        }
        List<Admin> list = list(queryWrapper).stream()
                .map(admin -> {
                    admin.setPassword(null);
                    return admin;
                }).collect(Collectors.toList());
        return list;
    }

    @Override
    public Result add(Admin admin) {
        Site site = siteService.getById(admin.getSiteId());
        if(site == null) return Result.error("该站点不存在！");
        admin.setId(null);
        admin.setSiteName(site.getName());
        boolean save = save(admin);
        return save ? Result.success() : Result.error("新增失败！");
    }

    @Override
    public Result changeStatus(Integer id) {
        Admin dbAdmin = getById(id);
        if(dbAdmin == null) throw new CustomException("该管理员不存在！");

        Admin admin = new Admin();
        admin.setId(id);
        if(dbAdmin.getStatus() == 1) admin.setStatus(0);
        else admin.setStatus(1);
        return updateById(admin) ? Result.success() : Result.error("修改失败！");
    }

    @Override
    public Result updateAdmin(Admin admin) {
        Admin dbAdmin = getById(admin.getId());
        if(dbAdmin == null) return Result.error("Admin Info Not Found!");

        UpdateWrapper<Admin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", admin.getId());

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if(admin.getUsername() != null && !admin.getUsername().equals("")) {
            queryWrapper.ne(Admin::getId, admin.getId()).eq(Admin::getUsername, admin.getUsername());
            Admin one = this.getOne(queryWrapper);
            if(one != null) return Result.error("该username已存在！");
            queryWrapper.clear();
            updateWrapper.set("username", admin.getUsername());
        }
        if(admin.getPhone() != null && !admin.getPhone().equals("")) {
            queryWrapper.ne(Admin::getId, admin.getId()).eq(Admin::getPhone, admin.getPhone());
            Admin one = this.getOne(queryWrapper);
            if(one != null) return Result.error("该手机号已被占用！");
            queryWrapper.clear();
            updateWrapper.set("phone", admin.getPhone());
        }
        if(admin.getSiteId() != dbAdmin.getSiteId()) {
            LambdaQueryWrapper<Site> siteQueryWrapper = new LambdaQueryWrapper<>();
            Site site = siteService.getById(admin.getSiteId());
            if(site == null) return Result.error("Site Info Not Found!");

            updateWrapper.set("site_id", admin.getSiteId());
            updateWrapper.set("site_name", site.getName());
        }
        if(admin.getName() != dbAdmin.getName()) {
            updateWrapper.set("name", admin.getName());
        }
        if(admin.getPassword() != null && !admin.getPassword().equals("") && !admin.getPassword().equals(dbAdmin.getPassword())) {
            updateWrapper.set("password", admin.getPassword());
        }

        return update(updateWrapper) ? Result.success() : Result.error("修改失败！");
    }
}
