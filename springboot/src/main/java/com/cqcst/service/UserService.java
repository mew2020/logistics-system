package com.cqcst.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqcst.common.Result;
import com.cqcst.entity.User;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service
* @createDate 2024-03-26 11:08:33
*/
public interface UserService extends IService<User> {
    Result login(User user);

    Result register(User user);

    Result updateUser(User user);

    Result updatePassword(Integer id, Map<String,String> data);
}
