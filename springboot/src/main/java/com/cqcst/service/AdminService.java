package com.cqcst.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqcst.common.Result;
import com.cqcst.entity.Admin;
import com.cqcst.exception.CustomException;
import com.cqcst.mapper.AdminMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public interface AdminService extends IService<Admin> {

    Admin login(Admin admin);

    void register(Admin admin);

    List<Admin> getAdminList(String condition, Integer status) ;

    Result add(Admin admin);

    Result changeStatus(Integer id);

    Result updateAdmin(Admin admin);
}
