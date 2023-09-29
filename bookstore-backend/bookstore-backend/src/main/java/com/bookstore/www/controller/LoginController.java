package com.bookstore.www.controller;

import com.bookstore.www.entity.Userinfo;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.service.ClockService;
import com.bookstore.www.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("users")
@Scope("session")
public class LoginController {

    private ClockService clockService;
    private UserService userService;
    @Autowired
    public LoginController(ClockService clockService, UserService userService){

        this.clockService = clockService;
        this.userService = userService;
    }

    @RequestMapping("/login")
    public Msg login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        System.out.println(username);
        System.out.println(password);
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(1000*60*60);
        if (userService.checkUserCredential(username, password)) {
            Userinfo userinfo = userService.getUserInfo(username);
            if(userinfo.getStatus() == 0){
                return new Msg("ban", null);
            }
            Msg result = new Msg("success", userinfo);
            if(session.getAttribute(userinfo.getId().toString()) != null
               && session.getAttribute(userinfo.getId().toString()).equals(session.getId())){
                return new Msg("failed", "你已经登录过了！");
            }

            session.setAttribute(userinfo.getId().toString(), session.getId());
            clockService.startClockCounting();

            return result;
        } else {
            Msg result = new Msg("failed", null);
            return result;
        }
    }


    @RequestMapping("/logout")
    public Msg logout(@RequestParam("username") String username, HttpServletRequest request){
        HttpSession session = request.getSession();
        String timeInterval = clockService.endClockCounting();
        session.removeAttribute(username);
        return new Msg("successful", timeInterval);
    }
}
