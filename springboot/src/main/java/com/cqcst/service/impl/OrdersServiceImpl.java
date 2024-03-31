package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.entity.Orders;
import com.cqcst.entity.Track;
import com.cqcst.mapper.OrdersMapper;
import com.cqcst.service.OrdersService;
import com.cqcst.service.TrackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Administrator
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2024-03-31 11:45:30
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

    @Resource
    TrackService trackService;

    public Orders getLastOrderByUserId(Integer uid) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, uid).orderByDesc(Orders::getCreateTime);
        queryWrapper.last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public Boolean addOrderByUser(Orders order) {
        order.setId(null);
        order.setStatus(0); //“已下单，待快递员接单”
        order.setIsDeleted(false);
        boolean flag1 = save(order);
        Orders dbOrder = getLastOrderByUserId(order.getUserId());
        System.out.println(dbOrder);
        if(dbOrder == null) return false;

        Track track = new Track();
        track.setOrderId(dbOrder.getId());
        track.setStatus(0);
        track.setLocation(dbOrder.getSenderAddress());
        track.setRemark("已下单");
        boolean flag2 = trackService.save(track);
        return flag1 && flag2;
    }

    @Override
    public List<Orders> getSenderListByUserInfo(Integer userId, String phone) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId).or().eq(Orders::getSenderPhone, phone);
        return list(queryWrapper);
    }

    @Override
    public List<Orders> getReceiverListByUserInfo(String phone) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getReceiverPhone, phone);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean cancelOrderByUser(Integer id) {
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(-1);
        boolean flag1 = updateById(order);
        Orders dbOrder = getById(id);

        Track track = new Track();
        track.setOrderId(id);
        track.setStatus(-1);
        track.setLocation(dbOrder.getSenderAddress());
        track.setRemark("用户已取消");
        boolean flag2 = trackService.save(track);
        return flag1 && flag2;
    }

    @Override
    public boolean deleteOrder(Integer id) {
        return removeById(id);
    }

    @Override
    public List<Orders> getSenderListByUserInfoAndCondition(Integer uid, String phone, String condition) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, uid).or().eq(Orders::getSenderPhone, phone)
                .and(q -> q.like(Orders::getSenderName, condition)
                        .or().like(Orders::getReceiverName, condition)
                        .or().like(Orders::getSenderPhone, condition)
                        .or().like(Orders::getReceiverPhone, condition)
                        .or().like(Orders::getId, condition)
                );
        return list(queryWrapper);
    }

    @Override
    public List<Orders> getReceiverListByUserInfoAndCondition(String phone, String condition) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getReceiverPhone, phone)
                .and(q -> q.like(Orders::getSenderName, condition)
                        .or().like(Orders::getReceiverName, condition)
                        .or().like(Orders::getSenderPhone, condition)
                        .or().like(Orders::getReceiverPhone, condition)
                        .or().like(Orders::getId, condition)
                );
        return list(queryWrapper);
    }
}




