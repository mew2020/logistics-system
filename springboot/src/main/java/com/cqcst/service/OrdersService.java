package com.cqcst.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqcst.entity.Orders;

import java.util.List;

/**
* @author Administrator
* @description 针对表【orders】的数据库操作Service
* @createDate 2024-03-31 11:45:30
*/
public interface OrdersService extends IService<Orders> {
    Boolean addOrderByUser(Orders order);

    List<Orders> getSenderListByUserInfo(Integer userId, String phone);

    List<Orders> getReceiverListByUserInfo(String phone);

    boolean cancelOrderByUser(Integer id);

    boolean deleteOrder(Integer id);

    List<Orders> getSenderListByUserInfoAndCondition(Integer uid, String phone, String condition);

    List<Orders> getReceiverListByUserInfoAndCondition(String phone, String condition);
}
