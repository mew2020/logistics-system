package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.Admin;
import com.cqcst.entity.Courier;
import com.cqcst.entity.Orders;
import com.cqcst.entity.User;
import com.cqcst.service.AdminService;
import com.cqcst.service.CourierService;
import com.cqcst.service.OrdersService;
import com.cqcst.service.UserService;
import com.cqcst.util.AdminContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    //调用service方法
    @Resource
    private AdminService adminService;

    @Resource
    private OrdersService orderService;

    @Resource
    private UserService userService;

    @Resource
    private CourierService courierService;

    @PostMapping("/login")
    public Result login(@RequestBody Admin admin){
        Admin dbAdmin = adminService.login(admin);
        if(dbAdmin != null) {
            AdminContext.setAdmin(dbAdmin);
        }
        return Result.success(dbAdmin);
    }
    @PostMapping("/register")
    public Result register(@RequestBody Admin admin){
        adminService.register(admin);
        return Result.success();
    }

    @GetMapping("/order/list")
    public Result getOrderList(@RequestParam(required = false) String condition, @RequestParam(required = false) Integer status){
        System.out.println("condition:" + condition + " status:" + status);
        List<Orders> list = orderService.getOrderListByAdmin(condition, status);
        return Result.success(list);
    }

    @PutMapping("/order/cancel")
    public Result cancelOrderByAdmin(@RequestBody Map<String, Integer> map){
        return orderService.cancelOrderByAdmin(map.get("adminId"), map.get("orderId"));
    }

    @GetMapping("/order/list/default")
    public Result getOrderListDefaultByAdmin(){
        List<Orders> list = orderService.getOrderListByAdmin(null, 0);
        return Result.success(list);
    }

    @GetMapping("/order/list/preentry/{siteId}")
    public Result getOrderListPreEntryByAdmin(@PathVariable Integer siteId,@RequestParam String condition){
        return orderService.getOrderListPreEntryByAdmin(siteId, condition);
    }

    @GetMapping("/order/list/entry/{siteId}")
    public Result getOrderListEntryByAdmin(@PathVariable Integer siteId,@RequestParam String condition){
        return orderService.getOrderListEntryByAdmin(siteId, condition);
    }

    @GetMapping("/order/list/out/{siteId}")
    public Result getOrderListOutByAdmin(@PathVariable Integer siteId,@RequestParam String condition){
        return orderService.getOrderListOutByAdmin(siteId, condition);
    }


    @GetMapping("/user/list")
    public Result getUserList(@RequestParam(required = false) String condition, @RequestParam(required = false) Integer status){
        System.out.println("condition:" + condition + " status:" + status);
        List<User> list = userService.getUserListByAdmin(condition, status);
        return Result.success(list);
    }

    @PutMapping("/user/status/{id}")
    public Result changeUserStatus(@PathVariable Integer id){
        boolean flag = userService.changeStatusByAdmin(id);
        return flag ? Result.success() : Result.error();
    }

    @PutMapping("/user/balance")
    public Result changeUserBalance(@RequestBody Map<String, Integer> map){
        //params:{id, status:(1,-1), amount}
        System.out.println(map);
        return userService.updateBalanceByAdmin(map);
    }

    @PutMapping("/courier/disabled")
    public Result changeCourierDisabled(@RequestBody Courier courier){
        boolean flag = courierService.changeDisabledByAdmin(courier);
        return flag ? Result.success() : Result.error();
    }

    @PostMapping("/courier/register")
    public Result courierRegister(@RequestBody Courier courier){
        boolean flag = courierService.registerByAdmin(courier);
        return flag ? Result.success() : Result.error();
    }

    @GetMapping("/courier/list")
    public Result courierList(@RequestParam Integer siteId){
        return courierService.listBySiteId(siteId);
    }

    @PutMapping("/courier/allocate")
    public Result allocateCourier(@RequestBody Map<String, Integer> map){
        return orderService.allocateCourierByAdmin(map);
    }

    @PutMapping("/courier/reallocate")
    public Result reallocateCourier(@RequestBody Map<String, Integer> map){
        return orderService.reallocateCourierByAdmin(map);
    }

    @PutMapping("/order/entry")
    public Result entryByAdmin(@RequestBody Map<String, Integer> map){
        return orderService.entryByAdmin(map);
    }

    @PutMapping("/order/out")
    public Result outByAdmin(@RequestBody Map<String, Integer> map){
        return orderService.outByAdmin(map);
    }

    @GetMapping("/list")
    public Result getAdminList(@RequestParam(required = false) String condition, @RequestParam(required = false) Integer status){
        return Result.success(adminService.getAdminList(condition, status));
    }

    @PostMapping("/add")
    public Result addAdmin(@RequestBody Admin admin) {
        return adminService.add(admin);
    }

    @PutMapping("/status/{id}")
    public Result changeStatus(@PathVariable Integer id) {
        return adminService.changeStatus(id);
    }

    @PutMapping("/update")
    public Result updateAdmin(@RequestBody Admin admin) {
        System.out.println(admin);
        return adminService.updateAdmin(admin);
    }

}
