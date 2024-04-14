package com.cqcst.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqcst.common.Result;
import com.cqcst.entity.Orders;

import java.util.List;
import java.util.Map;

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

    boolean updateOrderByUser(Orders order);

    List<Orders> getOrderListByAdmin(String condition, Integer status);

    Result allocateCourierByAdmin(Map<String, Integer> map);

    Result getDeliveryListByCourier(Integer id, String condition);

    Result getDeliveredListByCourier(Integer id, String condition);

    public Result getPickListByCourier(Integer id, String condition);

    public Result getPickedListByCourier(Integer id, String condition);

    Result reallocateCourierByAdmin(Map<String, Integer> map);

    Result getOrderListPreEntryByAdmin(Integer siteId, String condition);

    Result getOrderListEntryByAdmin(Integer siteId, String condition);

    Result getOrderListOutByAdmin(Integer siteId, String condition);

    Result cancelOrderByAdmin(Integer adminId, Integer orderId);

    Result cancelOrderByCourier(Integer courierId, Integer id, String condition);

    Result pickOrderByCourier(Map<String, String> map);

    Result payOrderByUser(Integer orderId);

    Result entryByAdmin(Map<String, Integer> map);

    Result outByAdmin(Map<String, Integer> map);

    Result deliveryOrderByCourier(Map<String, String> map);
}
