package com.bookstore.www.service;

import com.bookstore.www.dao.UserAccessService;
import com.bookstore.www.entity.User;
import com.bookstore.www.entity.Userinfo;
import com.bookstore.www.msg.Msg;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
    /*服务层，包含了应用的核心逻辑，提供了高一级的业务功能，暴露可供控制层调用的接口,
    协调不同的数据访问对象，处理事务管理*/

    private final UserAccessService userAccessService;

    public UserServiceImp(UserAccessService userAccessService) {
        this.userAccessService = userAccessService;
    }

    @Override
    public List<Userinfo> getAllUsers() {
        return userAccessService.selectAllUsers();
    }

    @Override
    public boolean checkUserCredential(String username, String password) {
        return this.userAccessService.checkUser(username, password);
    }

    @Override
    public Userinfo getUserInfo(String username) {
        return this.userAccessService.getInfo(username);
    }

    @Override
    public Msg banUser(String user_id) {
        return this.userAccessService.banUser(user_id);
    }

    @Override
    public Msg unbanUser(String user_id) {
        return this.userAccessService.unbanUser(user_id);
    }

    @Override
    public boolean checkAdmin(String id) {
        return userAccessService.checkAdmin(id);
    }

    @Override
    public Msg getUserById(String userId) {
        return userAccessService.getUserById(userId);
    }

    @Override
    public Msg register(User newuser, Userinfo newUserinfo) {
        return userAccessService.register(newuser, newUserinfo);
    }
}
