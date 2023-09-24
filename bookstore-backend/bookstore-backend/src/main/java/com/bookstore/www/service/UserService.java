package com.bookstore.www.service;
import com.bookstore.www.entity.User;
import com.bookstore.www.dao.UserAccessService;
import com.bookstore.www.entity.Userinfo;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    /*服务层，包含了应用的核心逻辑，提供了高一级的业务功能，暴露可供控制层调用的接口,
    协调不同的数据访问对象，处理事务管理*/
    public List<Userinfo> getAllUsers();

    public boolean checkUserCredential(String username, String password);
    public Userinfo getUserInfo(String username);

    public Msg banUser(String user_id);

    public Msg unbanUser(String user_id);
    public boolean checkAdmin(String id);

    public Msg getUserById(String userId);

    public Msg register(User newuser, Userinfo newUserinfo);
}


