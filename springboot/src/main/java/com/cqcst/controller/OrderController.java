package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.Orders;
import com.cqcst.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    OrdersService orderService;

    @PostMapping("/user/add")
    public Result addOrderByUser(@RequestBody Orders order)
    {
        System.out.println(order);
        return orderService.addOrderByUser(order) == true? Result.success() : Result.error();
    }


    @GetMapping("/user/list/sender")
    public Result getSenderListByUserInfo(@RequestParam Integer id, @RequestParam String phone) {
        List<Orders> list = orderService.getSenderListByUserInfo(id, phone);
        return Result.success(list);
    }

    @GetMapping("/user/list/sender/condition")
    public Result getSenderListByUserInfoAndCondition(@RequestParam Integer uid, @RequestParam String phone, @RequestParam String condition) {
        List<Orders> list = orderService.getSenderListByUserInfoAndCondition(uid, phone, condition);
        return Result.success(list);
    }
    @GetMapping("/user/list/receiver/condition")
    public Result getSenderListByUserInfoAndCondition(@RequestParam String phone, @RequestParam String condition) {
        List<Orders> list = orderService.getReceiverListByUserInfoAndCondition(phone, condition);
        return Result.success(list);
    }

    @GetMapping("/user/list/receiver")
    public Result getReceiverListByUserInfo(@RequestParam String phone){
        List<Orders> list = orderService.getReceiverListByUserInfo(phone);
        return Result.success(list);
    }

    @PutMapping("/user/cancel/{id}")
    public Result cancelOrderByUser(@PathVariable Integer id){
        System.out.println(id);
        boolean flag = orderService.cancelOrderByUser(id);
        return flag == true ? Result.success() : Result.error();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteOrder(@PathVariable Integer id){
        boolean flag = orderService.deleteOrder(id);
        return flag == true ? Result.success() : Result.error();
    }

    @GetMapping
    public Result getOrderById(@RequestParam Integer id){
        Orders order = orderService.getById(id);
        return Result.success(order);
    }

    @PutMapping("/user/update")
    public Result updateOrderByUser(@RequestBody Orders order){
        System.out.println(order);
        return orderService.updateOrderByUser(order) == true ? Result.success() : Result.error();
    }

    @PutMapping("/user/pay")
    public Result payOrderByUser(@RequestBody Map<String, Integer> map){
        return orderService.payOrderByUser(map.get("orderId"));
    }

    @GetMapping("/courier/list/delivery/{courierId}")
    public Result getDeliveryListByCourier(@PathVariable Integer courierId, @RequestParam(required = false) String condition){
        return orderService.getDeliveryListByCourier(courierId,condition);
    }

    @GetMapping("/courier/list/delivered/{courierId}")
    public Result getDeliveredListByCourier(@PathVariable Integer courierId, @RequestParam(required = false) String condition){
        return orderService.getDeliveredListByCourier(courierId,condition);
    }

    @GetMapping("/courier/list/pick/{courierId}")
    public Result getPickListByCourier(@PathVariable Integer courierId, @RequestParam(required = false) String condition){
        return orderService.getPickListByCourier(courierId,condition);
    }

    @GetMapping("/courier/list/picked/{courierId}")
    public Result getPickedListByCourier(@PathVariable Integer courierId, @RequestParam(required = false) String condition){
        return orderService.getPickedListByCourier(courierId,condition);
    }

    @PutMapping("/courier/cancel")
    public Result cancelOrderByCourier(@RequestBody Map<String, String> map){
        return orderService.cancelOrderByCourier(Integer.parseInt(map.get("courierId")), Integer.parseInt(map.get("orderId")), map.get("remark"));
    }

    @PutMapping("/courier/pick")
    public Result pickOrderByCourier(@RequestBody Map<String, String> map){
        return orderService.pickOrderByCourier(map);
    }

    @PutMapping("/courier/delivery")
    public Result deliveryOrderByCourier(@RequestBody Map<String, String> map){
        return orderService.deliveryOrderByCourier(map);
    }

}
