package com.cqcst.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqcst.common.Result;
import com.cqcst.entity.*;
import com.cqcst.mapper.OrdersMapper;
import com.cqcst.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    AdminService adminService;

    @Resource
    CourierService courierService;

    @Resource
    SiteService siteService;

    @Resource
    UserService userService;

    public Orders getLastOrderByUserId(Integer uid) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, uid).orderByDesc(Orders::getCreateTime);
        queryWrapper.last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public Result payOrderByUser(Integer orderId) {
        Orders order = getById(orderId);
        if(order == null) return Result.error("Order Info Not Found");

        User user = userService.getById(order.getUserId());
        if(user == null) return Result.error("User Info Not Found");

        double newBalance = user.getBalance() - order.getPrice();
        if(newBalance < 0) return Result.error("Insufficient Balance");

        Track dbTrack = trackService.getOne(new LambdaQueryWrapper<Track>().eq(Track::getOrderId, order.getId()).eq(Track::getStatus, 2).orderByDesc(Track::getCreateTime).last("limit 1"));
        Track track = new Track();
        track.setOrderId(order.getId());
        track.setStatus(4);
        track.setRemark("用户已支付。");
        track.setSiteId(dbTrack.getSiteId());
        track.setSiteName(dbTrack.getSiteName());
        boolean flag1 = trackService.save(track);
        boolean flag2 = userService.update(new UpdateWrapper<User>().eq("id", user.getId()).set("balance", newBalance));
        boolean flag3 = this.update(new UpdateWrapper<Orders>().eq("id", orderId).set("status", 4));
        return flag1 && flag2 && flag3 ? Result.success() : Result.error();
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
        queryWrapper.eq(Orders::getUserId, userId).or().eq(Orders::getSenderPhone, phone).orderByDesc(Orders::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<Orders> getReceiverListByUserInfo(String phone) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getReceiverPhone, phone).orderByDesc(Orders::getCreateTime);
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

    @Override
    @Transactional
    public Result cancelOrderByAdmin(Integer adminId, Integer orderId) {
        Admin admin = adminService.getById(adminId);
        if(admin == null) return Result.error("Admin Info Not Found");

        Orders dbOrder = this.getById(orderId);
        if(dbOrder == null) return Result.error("Order Info Not Found");

        Track track = new Track();
        track.setOrderId(orderId);
        track.setAdminId(adminId);
        track.setAdminName(admin.getName());
        track.setSiteId(admin.getSiteId());
        track.setSiteName(admin.getSiteName());
        track.setStatus(-1);
        track.setRemark("该订单已被站点【" + admin.getSiteName() + "】的管理员【" + admin.getName() + "】取消！");
        boolean flag1 = trackService.save(track);

        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus(-1);
        boolean flag2 = this.updateById(order);

        return flag1 && flag2 ? Result.success() : Result.error();
    }

    @Override
    @Transactional
    public boolean updateOrderByUser(Orders order) {
        Orders dbOrder = this.getById(order.getId());
        boolean flag1 = updateById(order);

        Track track = new Track();
        track.setOrderId(order.getId());
        track.setRemark("用户已修改订单");
        if(!dbOrder.getSenderAddress().equals(order.getSenderAddress())) {
            track.setLocation(order.getSenderAddress());
        }
        boolean flag2 = trackService.save(track);
        return flag1 && flag2;
    }

    @Override
    @Transactional
    public Result entryByAdmin(Map<String, Integer> map) {
        Integer orderId = map.get("orderId");
        Orders order = this.getById(orderId);
        if (order == null) return Result.error("Order Info Not Found");

        Integer adminId = map.get("adminId");
        Admin admin = adminService.getById(adminId);
        if(admin == null) return Result.error("Admin Info Not Found");

        Site site = siteService.getById(admin.getSiteId());

        Track track = new Track();
        track.setOrderId(orderId);
        track.setSiteId(admin.getSiteId());
        track.setSiteName(admin.getSiteName());
        track.setAdminId(adminId);
        track.setAdminName(admin.getName());
        track.setLocation(site.getLocation());

        if(order.getStatus() == 4) {
            //此时是初次入库
            track.setStatus(5);
            track.setRemark("快递已于站点【" + site.getName() + "】入库。（操作员：" + admin.getName() + ")");
            boolean flag1 = trackService.save(track);
            boolean flag2 = this.update(new UpdateWrapper<Orders>().eq("id", orderId).set("status", 5));
            return flag1 && flag2 ? Result.success() : Result.error();
        } else {
            //status == 6，运输过程中
            Integer arriveFlag = map.get("arriveFlag");
            if(arriveFlag == 0) {
               //没有到达最终站点，不会直接变更到待派送状态
                track.setStatus(6);
                track.setRemark("快递已抵达站点【" + site.getName() + "】。（操作员：" + admin.getName() + ")");
                return trackService.save(track) ? Result.success() : Result.error();
            } else {
                //到达最终站点，变更到待派送状态
                track.setStatus(7);
                track.setRemark("快递已抵达站点【" + site.getName() + "】。（操作员：" + admin.getName() + ")");
                boolean flag1 = trackService.save(track);
                boolean flag2 = this.update(new UpdateWrapper<Orders>().eq("id", orderId).set("status", 7));
                return flag1 && flag2 ? Result.success() : Result.error();
            }
        }
    }

    @Override
    public Result outByAdmin(Map<String, Integer> map) {
        Integer toSiteId = map.get("toSiteId");
        Site toSite = siteService.getById(toSiteId);
        if(toSite == null) return Result.error("toSite Info Not Found");

        Integer adminId = map.get("adminId");
        Admin admin = adminService.getById(adminId);
        if(admin == null) return Result.error("Admin Info Not Found");
        else if(admin.getSiteId() == toSiteId) return Result.error("fromSiteId == toSiteId");

        Integer orderId = map.get("orderId");
        Orders order = this.getById(orderId);
        if (order == null) return Result.error("Order Info Not Found");

        Track track = new Track();
        track.setStatus(6);
        track.setOrderId(orderId);
        track.setSiteId(admin.getSiteId());
        track.setSiteName(admin.getSiteName());
        track.setAdminId(adminId);
        track.setAdminName(admin.getName());
        track.setToSiteId(toSiteId);
        track.setToSiteName(toSite.getName());
        track.setRemark("快递已从站点【" + admin.getSiteName() + "】出发，发往站点【" + toSite.getName() + "】。（操作员：" + admin.getName() + ")");

        if(order.getStatus() == 5) {
            this.update(new UpdateWrapper<Orders>().eq("id", orderId).set("status", 6));
        }

        return trackService.save(track) ? Result.success() : Result.error();
    }


    @Override
    public List<Orders> getOrderListByAdmin(String condition, Integer status) {
        if(condition == null && status == null) return list();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if(status != null) queryWrapper.eq(Orders::getStatus, status);
        if(condition != null && !condition.equals("")) {
            queryWrapper.like(Orders::getSenderName, condition)
                    .or().like(Orders::getReceiverName, condition)
                    .or().like(Orders::getSenderPhone, condition)
                    .or().like(Orders::getReceiverPhone, condition)
                    .or().like(Orders::getId, condition);
        }
        queryWrapper.orderByDesc(Orders::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public Result getOrderListPreEntryByAdmin(Integer siteId, String condition) {
        if(siteId == null) return Result.error("siteId is null!");

        //pre-entry两种情况：
        //1. 已分配快递员，0 < order.status < 5  && track.siteId == siteId
        //2. 运输中，order.status == 6 && limit1：track.toSiteId == siteId
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.or(wrapper -> wrapper.ge(Orders::getStatus, 1).or().le(Orders::getStatus, 4)).or(wrapper -> wrapper.eq(Orders::getStatus, 6));

        if(condition != null && !condition.equals("")) {
            queryWrapper.and(
                    wrapper -> wrapper.like(Orders::getSenderName, condition)
                    .or().like(Orders::getReceiverName, condition)
                    .or().like(Orders::getSenderPhone, condition)
                    .or().like(Orders::getReceiverPhone, condition)
                    .or().like(Orders::getId, condition)
            );
        }

        List<Orders> list = list(queryWrapper).stream().filter(order -> {
            LambdaQueryWrapper<Track> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Track::getOrderId, order.getId()).orderByDesc(Track::getCreateTime).last("limit 1");
            Track one = trackService.getOne(wrapper);

            if(one == null) return false;
            Integer oneStatus = one.getStatus();
            if(oneStatus == 6 && one.getToSiteId() != null && one.getToSiteId().equals(siteId)) return true;
            else if(oneStatus > 0 && oneStatus < 5 && one.getSiteId() != null && one.getSiteId().equals(siteId)) return true;
            else return false;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result getOrderListEntryByAdmin(Integer siteId, String condition) {
        //Entry两种情况：
        //1. order.status == 5 && track.siteId == siteId
        //2. 运输中，order.status == 6 && limit1：track.siteId == siteId && track.toSiteId == null
        if(siteId == null) return Result.error("siteId is null!");
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> {
            wrapper.eq(Orders::getStatus, 5).or().eq(Orders::getStatus, 6);
        });
        if(condition != null && !condition.equals("")) {
            queryWrapper.and(
                    wrapper -> wrapper.like(Orders::getSenderName, condition)
                            .or().like(Orders::getReceiverName, condition)
                            .or().like(Orders::getSenderPhone, condition)
                            .or().like(Orders::getReceiverPhone, condition)
                            .or().like(Orders::getId, condition)
            );
        }

        List<Orders> list = list(queryWrapper).stream().filter(order -> {
            Track track = trackService.getOne(new LambdaQueryWrapper<Track>().eq(Track::getOrderId, order.getId()).orderByDesc(Track::getCreateTime).last("limit 1"));
            if(track == null) return false;

            Integer status = track.getStatus();
            if(status == 5 && track.getSiteId() == siteId) return true;
            else if(status == 6 && track.getSiteId() == siteId && track.getToSiteId() == null) return true;
            else return false;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result getOrderListOutByAdmin(Integer siteId, String condition) {
        //Out情况：order.status >= 6 && [(track.status == 5 && track.siteId == siteId) || (track.status == 6 && track.siteId == siteId)]
        if(siteId == null) return Result.error("siteId is null!");
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Orders::getStatus, 6);
        if(condition != null && !condition.equals("")) {
            queryWrapper.and(
                    wrapper -> wrapper.like(Orders::getSenderName, condition)
                            .or().like(Orders::getReceiverName, condition)
                            .or().like(Orders::getSenderPhone, condition)
                            .or().like(Orders::getReceiverPhone, condition)
                            .or().like(Orders::getId, condition)
            );
        }

        List<Orders> list = list(queryWrapper).stream().filter(order -> {
            List<Track> tracks = trackService.list(new LambdaQueryWrapper<Track>().eq(Track::getOrderId, order.getId()).ge(Track::getStatus, 5).orderByAsc(Track::getCreateTime));
            for(Track track : tracks) {
                Integer status = track.getStatus();
                if(status == 5 && track.getSiteId().equals(siteId)) return true; //当入库时是该site，但已经出库(前面已经限制order.status > 5)
                else if(status == 6 && track.getSiteId().equals(siteId) && track.getToSiteId() != null) return true; //运输中到达过该site，但已经出库
            }
            return false;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result deliveryOrderByCourier(Map<String, String> map) {
        Integer courierId = Integer.parseInt(map.get("courierId"));
        Courier courier = courierService.getById(courierId);
        if(courier == null) return Result.error("Courier Info Not Found");

        Integer orderId = Integer.parseInt(map.get("orderId"));
        Orders order = this.getById(orderId);
        if (order == null) return Result.error("Order Info Not Found");

        String remark = map.get("remark");
        if(remark == null) return Result.error("Remark is null!");

        Track track = new Track();
        track.setOrderId(orderId);
        track.setStatus(9);
        track.setCourierId(courierId);
        track.setCourierName(courier.getName());
        track.setSiteId(courier.getSiteId());
        track.setSiteName(courier.getSiteName());
        track.setLocation(order.getReceiverAddress());
        track.setRemark("订单已签收。【" + remark + "】");

        boolean flag1 = trackService.save(track);
        boolean flag2 = this.update(new UpdateWrapper<Orders>().eq("id", orderId).set("status", 9));

        return flag1 && flag2? Result.success() : Result.error();
    }

    @Override
    public Result cancelOrderByCourier(Integer courierId, Integer OrderId, String remark) {
        Courier courier = courierService.getById(courierId);
        if(courier == null) return Result.error("Courier Info Not Found");

        Orders dbOrder = this.getById(OrderId);
        if(dbOrder == null) return Result.error("Order Info Not Found");

        Track track = new Track();
        track.setStatus(-1);
        track.setOrderId(OrderId);
        track.setCourierId(courierId);
        track.setCourierName(courier.getName());
        track.setSiteId(courier.getSiteId());
        track.setSiteName(courier.getSiteName());
        track.setRemark("快递员【" + courier.getName() + "】因【" + remark + "】取消该订单。");

        Orders order = new Orders();
        order.setId(OrderId);
        order.setStatus(-1);
        boolean flag1 = this.updateById(order);
        boolean flag2 = trackService.save(track);
        return flag1 && flag2 ? Result.success() : Result.error();
    }

    @Override
    public Result pickOrderByCourier(Map<String, String> map) {
        Integer orderId = Integer.parseInt(map.get("orderId"));
        Orders dbOrder = this.getById(orderId);
        if(dbOrder == null) return Result.error("Order Info Not Found");

        Integer courierId = Integer.parseInt(map.get("courierId"));
        Courier courier = courierService.getById(courierId);
        if(courier == null) return Result.error("Courier Info Not Found");

        String goods = map.get("goods");
        double weight = Double.parseDouble(map.get("weight"));
        double price = Double.parseDouble(map.get("price"));
        if(goods == null || weight == 0) return Result.error("goods is null or weight is invalid!");

        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus(2);
        order.setGoods(goods);
        order.setWeight(weight);
        order.setPrice(price);
        boolean flag1 = this.updateById(order);

        Track track = new Track();
        track.setOrderId(orderId);
        track.setStatus(2);
        track.setCourierId(courier.getId());
        track.setCourierName(courier.getName());
        track.setSiteId(courier.getSiteId());
        track.setSiteName(courier.getSiteName());
        track.setRemark("快递员【" + courier.getName() + "】已取件，订单待支付。");
        boolean flag2 = trackService.save(track);
        return flag1 && flag2 ? Result.success() : Result.error();
    }

    @Override
    @Transactional
    public Result allocateCourierByAdmin(Map<String, Integer> map) {
        Integer orderId = map.get("orderId");
        Orders dbOrder = getById(orderId);
        if(dbOrder == null) return Result.error("Order Info Not Found");

        Track track = new Track();
        track.setOrderId(orderId);

        //设置通用信息
        //1.获取admin
        Integer adminId = map.get("adminId");
        Admin admin = adminService.getById(adminId);
        if(admin == null) return Result.error("Admin Info Not Found");
        track.setAdminId(adminId);
        track.setAdminName(admin.getName());

        //2.获取快递员信息
        Integer courierId = map.get("courierId");
        Courier courier = courierService.getById(courierId);
        if(courier == null) return Result.error("Courier Info Not Found");
        track.setCourierId(courierId);
        track.setCourierName(courier.getName());
        User user = userService.getById(courier.getId());
        track.setCourierPhone(user.getPhone());

        //3.获取站点信息
        Site site = siteService.getById(courier.getSiteId());
        if(site == null) return Result.error("Site Info Not Found");
        track.setSiteId(site.getId());
        track.setSiteName(site.getName());

        //4.设置订单更新信息
        Orders order = new Orders();
        order.setId(orderId);

        //判断是进行的哪种初分配
        if(dbOrder.getStatus() == 0) {
            //刚下单，分配取件快递员

            //1.设置track信息
            track.setStatus(1);
            track.setRemark("已分配快递员【" + courier.getName() + "(" + user.getPhone() + ")】上门取件，请保持电话畅通。");

            //2.设置order信息
            order.setStatus(1);
            order.setPickerId(courierId);
            order.setPickerName(courier.getName());



        } else {
            //分配派件快递员

            //1.设置track信息
            track.setStatus(8);
            track.setRemark("已分配快递员【" + courier.getName() + "(" + user.getPhone() + ")】上门派送，请保持电话畅通。");

            //2.设置order信息
            order.setStatus(8);
            order.setDispatcherId(courierId);
            order.setDispatcherName(courier.getName());

        }
        boolean flag1 = this.updateById(order);
        boolean flag2 = trackService.save(track);

        return flag1 && flag2 ? Result.success() : Result.error();
    }

    @Override
    public Result reallocateCourierByAdmin(Map<String, Integer> map) {
        Integer orderId = map.get("orderId");
        Orders dbOrder = getById(orderId);
        if(dbOrder == null) return Result.error("Order Info Not Found");

        Track track = new Track();
        track.setOrderId(orderId);

        //设置通用信息
        //1.获取admin
        Integer adminId = map.get("adminId");
        Admin admin = adminService.getById(adminId);
        if(admin == null) return Result.error("Admin Info Not Found");
        track.setAdminId(adminId);
        track.setAdminName(admin.getName());

        //2.获取快递员信息
        Integer courierId = map.get("courierId");
        Courier courier = courierService.getById(courierId);
        if(courier == null) return Result.error("Courier Info Not Found");
        track.setCourierId(courierId);
        track.setCourierName(courier.getName());
        User user = userService.getById(courier.getId());
        track.setCourierPhone(user.getPhone());

        //3.获取站点信息
        Site site = siteService.getById(courier.getSiteId());
        if(site == null) return Result.error("Site Info Not Found");
        track.setSiteId(site.getId());
        track.setSiteName(site.getName());

        //4.设置订单更新信息
        Orders order = new Orders();
        order.setId(orderId);

        //判断是进行的哪种再分配
        if(dbOrder.getStatus() == 1) {
            //再分配取件快递员

            //1.设置track信息
            track.setStatus(1);
            track.setRemark("已更换快递员。快递员【" + courier.getName() + "(" + user.getPhone() + ")】将上门取件，请保持电话畅通。");

            //2.设置order信息
            order.setPickerId(courierId);
            order.setPickerName(courier.getName());

        } else {
            //再分配派件快递员

            //1.设置track信息
            track.setStatus(8);
            track.setRemark("已更换快递员。快递员【" + courier.getName() + "(" + user.getPhone() + ")】将上门派送，请保持电话畅通。");

            //2.设置order信息
            order.setDispatcherId(courierId);
            order.setDispatcherName(courier.getName());

        }
        boolean flag1 = this.updateById(order);
        boolean flag2 = trackService.save(track);

        return flag1 && flag2 ? Result.success() : Result.error();
    }

    @Override
    public Result getDeliveryListByCourier(Integer id, String condition) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getDispatcherId, id).eq(Orders::getStatus, 8);
        if(condition != null && !condition.equals("")) {
            queryWrapper.and(wrapper -> wrapper.like(Orders::getId, condition)
                    .or().like(Orders::getReceiverName, condition)
                    .or().like(Orders::getReceiverPhone, condition)
            );
        }
        return Result.success(list(queryWrapper));
    }

    @Override
    public Result getDeliveredListByCourier(Integer id, String condition) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getDispatcherId, id).eq(Orders::getStatus, 9);
        if(condition != null && !condition.equals("")) {
            queryWrapper.and(wrapper -> wrapper.like(Orders::getId, condition)
                    .or().like(Orders::getReceiverName, condition)
                    .or().like(Orders::getReceiverPhone, condition)
            );
        }
        return Result.success(list(queryWrapper));
    }

    @Override
    public Result getPickListByCourier(Integer id, String condition) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getPickerId, id).eq(Orders::getStatus, 1);
        if(condition != null && !condition.equals("")) {
            queryWrapper.and(wrapper -> wrapper.like(Orders::getId, condition)
                    .or().like(Orders::getSenderName, condition)
                    .or().like(Orders::getSenderPhone, condition)
            );
        }
        return Result.success(list(queryWrapper));
    }

    @Override
    public Result getPickedListByCourier(Integer id, String condition) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if(condition != null && !condition.equals("")) {
            queryWrapper.and(wrapper -> wrapper.like(Orders::getId, condition)
                    .or().like(Orders::getSenderName, condition)
                    .or().like(Orders::getSenderPhone, condition)
            );
        }
        queryWrapper.eq(Orders::getPickerId, id).ge(Orders::getStatus, 2);
        return Result.success(list(queryWrapper));
    }
}




