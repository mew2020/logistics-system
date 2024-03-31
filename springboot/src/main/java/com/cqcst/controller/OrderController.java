package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.Orders;
import com.cqcst.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @PutMapping("/user/cancel")
    public Result cancelOrderByUser(@RequestParam Integer id){
        boolean flag = orderService.cancelOrderByUser(id);
        return flag == true ? Result.success() : Result.error();
    }

    @DeleteMapping("/delete")
    public Result deleteOrder(@RequestParam Integer id){
        boolean flag = orderService.deleteOrder(id);
        return flag == true ? Result.success() : Result.error();
    }

    @GetMapping
    public Result getOrderById(@RequestParam Integer id){
        Orders order = orderService.getById(id);
        return Result.success(order);
    }
}
