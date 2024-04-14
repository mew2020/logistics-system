package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.common.Result;
import com.cqcst.entity.Courier;
import com.cqcst.entity.Orders;
import com.cqcst.entity.User;
import com.cqcst.mapper.UserMapper;
import com.cqcst.service.CourierService;
import com.cqcst.service.UserAvatarService;
import com.cqcst.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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

    @Resource
    private CourierService courierService;

    @Override
    public Result login(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());

        User dbUser = getOne(queryWrapper);

        if(dbUser == null) return Result.error("NotExisted");
        else if(dbUser.getStatus() == 2) {
            //快递员 查信息
            Courier courier = courierService.getById(dbUser.getId());
            dbUser.setName(courier.getName());
            dbUser.setDisabled(courier.getDisabled());
            dbUser.setSiteId(courier.getSiteId());
            dbUser.setSiteName(courier.getSiteName());
        }
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

    @Override
    public List<User> getUserListByAdmin(String condition, Integer status) {
        if(condition == null && status == null) {
            List<User> list = list();
            for(User user : list) {
                Courier courier = courierService.getById(user.getId());
                if(courier != null) {
                    user.setName(courier.getName());
                    user.setDisabled(courier.getDisabled());
                    user.setSiteId(courier.getSiteId());
                    user.setSiteName(courier.getSiteName());
                }

            }
            return list;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if(status != null) queryWrapper.eq(User::getStatus, status);
        if(condition != null && !condition.equals("")) {
            queryWrapper.like(User::getUsername, condition)
                    .or().like(User::getPhone, condition)
                    .or().like(User::getNickname, condition);
        }
        List<User> list = list(queryWrapper);
        for(User user : list) {
            if(user.getStatus() != 1) {
                Courier courier = courierService.getById(user.getId());
                if(courier != null) {
                    user.setName(courier.getName());
                    user.setDisabled(courier.getDisabled());
                    user.setSiteId(courier.getSiteId());
                    user.setSiteName(courier.getSiteName());
                }
            }
        }

        return list;
    }

    @Override
    public boolean changeStatusByAdmin(Integer id) {
        User user = getById(id);
        if(user == null) throw new RuntimeException("User not found");

        Integer status = user.getStatus();
        if(status == 0) {
            //先去查看有没有快递员数据，如果有且未被禁用，则将其设置为快递员(user.status = 2)，如果被禁用或没有快递员数据，则将其设置为用户(user.status = 1)
            Courier courier = courierService.getById(id);
            if(courier == null || courier.getDisabled() == true) {
                user.setStatus(1);
            } else {
                user.setStatus(2);
            }
            return updateById(user);
        } else {
            //直接将其停权即可
            user.setStatus(0);
            return updateById(user);
        }
    }

    @Override
    public Result updateBalanceByAdmin(Map<String, Integer> map) {
        Integer id = map.get("id");
        User dbUser = getById(id);
        if(dbUser == null) return Result.error("User not found");

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        String sql;
        if(map.get("status") == -1) {
            sql = "balance = balance - " + map.get("amount");
        } else {
            sql = "balance = balance + " + map.get("amount");
        }
        updateWrapper.setSql(sql);
        boolean update = update(updateWrapper);

        return update?Result.success():Result.error("余额变更失败！");
    }

    @Override
    public Result getUserBalance(Integer id) {
        User user = this.getById(id);
        return user == null ? Result.error("User not found") : Result.success(user.getBalance());
    }
}




