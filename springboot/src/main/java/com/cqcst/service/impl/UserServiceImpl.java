package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.common.Result;
import com.cqcst.entity.User;
import com.cqcst.mapper.UserMapper;
import com.cqcst.service.UserAvatarService;
import com.cqcst.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-03-26 11:08:33
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserAvatarService userAvatarService;

    @Override
    public Result login(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());

        User dbUser = getOne(queryWrapper);

        if(dbUser == null) return Result.error("NotExisted");
        dbUser.setPassword(null); //如果不为null，则设置密码为null后再回传

        //查询头像是否已经上传过，如果上传过就返回一个msg标识，否则不返回标识
        return userAvatarService.getById(dbUser.getId()) == null ? Result.success(dbUser) : Result.success(dbUser,"Existed");
    }

    @Override
    public Result register(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User dbUser = getOne(queryWrapper);
        if(dbUser != null) {
            return Result.error("ExistedUsername");
        }

        queryWrapper.clear(); //清空查询条件
        queryWrapper.eq(User::getPhone, user.getPhone());
        dbUser = getOne(queryWrapper);
        if(dbUser != null) {
            return Result.error("ExistedPhone");
        }

        //手动设置一下，防止id插入出现问题
        user.setId(null);
        user.setStatus(1);
        user.setBalance(0.0);
        boolean flag = save(user);
        return flag == true ? Result.success() : Result.error("Error");
    }

    @Override
    public Result updateUser(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(User::getId, user.getId()); //排除自己

        //查询手机号是否已存在
        queryWrapper.eq(User::getPhone, user.getPhone());
        User dbUser = getOne(queryWrapper);
        if(dbUser != null) {
            return Result.error("ExistedPhone");
        }

        return updateById(user) == true ? Result.success() : Result.error("Error");
    }

    @Override
    public Result updatePassword(Integer id, Map<String,String> data) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id).eq(User::getPassword, data.get("oldPassword"));
        if(getOne(queryWrapper) == null) {
            return Result.error("NotMatched");
        }

        User user = new User();
        user.setId(id);
        user.setPassword(data.get("newPassword"));
        return updateById(user) == true ? Result.success() : Result.error("Error");
    }
}




