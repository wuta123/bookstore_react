package com.bookstore.www.controller;

import com.bookstore.www.entity.User;
import com.bookstore.www.entity.Userinfo;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.service.ClockService;
import com.bookstore.www.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController {

    /*用户界面和应用的接口，负责请求转发、参数解析、处理用户请求，调用服务层的方法*/
    private UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<Userinfo> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/getbyid")
    public Msg getUserById(@RequestParam("user_id") String user_id){ return userService.getUserById(user_id);};



    @RequestMapping("/register")
    public Msg register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, @RequestParam("image") String image){
        User newuser = new User(UUID.randomUUID(), email, username, password,  image, false,0.0);
        Userinfo newUserinfo = new Userinfo(newuser.getId(),email,username,image,1,false);
        return userService.register(newuser, newUserinfo);
    }

    @RequestMapping("/user/ban")
    public Msg banUser(@RequestParam("user_id") UUID user_id, @RequestParam("admin_id") UUID admin_id){
        System.out.println("admin:"+ admin_id+" try to ban user:"+user_id);
        if(!checkAdmin(admin_id.toString())){
            System.out.println("failed");
            return new Msg("failed", null);
        }
        else{
            System.out.println("success");
            return userService.banUser(user_id.toString());
        }
    }

    @RequestMapping("/user/unban")
    public Msg unbanUser(@RequestParam("user_id") String user_id, @RequestParam("admin_id") String admin_id){
        System.out.println("admin:"+ admin_id+" try to unban user:"+user_id);
        if(!checkAdmin(admin_id)){
            System.out.println("failed");
            return new Msg("admin id is not valid!", null);
        }
        else{
            System.out.println("success");
            return userService.unbanUser(user_id);
        }
    }

    @RequestMapping("/check")
    public boolean checkAdmin(@RequestParam("id") String id){
        boolean result = userService.checkAdmin(id);
        return result;
    }

}
