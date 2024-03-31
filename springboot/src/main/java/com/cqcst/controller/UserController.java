package com.cqcst.controller;

import com.cqcst.common.Result;
import com.cqcst.entity.User;
import com.cqcst.service.UserAvatarService;
import com.cqcst.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private UserAvatarService userAvatarService;

    //@RequestBody传递的是Body的row形式
    @PostMapping("/login")
    public Result getUser(@RequestBody User user){
        Result result = userService.login(user);
        return result.getCode().equals("500") ? Result.error() : result;
    }

    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        user.setPassword(null);
        return user == null ? Result.error() : Result.success(user);
    }

//    //确认是否有该生   http://localhost:9090/user/cha/666666
//    @GetMapping("/cha/{xuehao}")
//    public Result selectx(@PathVariable Integer xuehao){
//        PageInfo<Student> studentPageInfo=studentService.selectx(xuehao);
//        return Result.success(studentPageInfo);
//    }

    @PostMapping("/register")
    public Result register(@RequestBody User user){
        Result res = userService.register(user);
        return res.getCode().equals("500") ? Result.error(res.getMsg()) : Result.success();
    }

    @PutMapping
    public Result updateUser(@RequestBody User user) {
        user.setPassword(null); //手动设置 预防密码被修改
        user.setBalance(null);
        user.setUsername(null);
        user.setStatus(null);
        return userService.updateUser(user);
    }

    @PutMapping("/password/{id}")
    public Result updatePassword(@PathVariable Integer id, @RequestBody Map<String,String> data) {
        for(String key : data.keySet()) {
            System.out.println(key + ":" + data.get(key));
        }
        return userService.updatePassword(id, data);
    }

    @PostMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam Integer id , MultipartFile avatar) {
        boolean flag = userAvatarService.updateAvatar(id, avatar);
        return flag == true ? Result.success("success") : Result.error("error");
    }
    @GetMapping("/getAvatar/{id}")
    public void getAvatar (@PathVariable Integer id, HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        System.out.println("id:" + id);
        Blob avatar = userAvatarService.getAvatar(id);
        if(avatar == null) return;

        System.out.println(avatar);
        try {
            response.getOutputStream().write(avatar.getBytes(1, (int) avatar.length()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
