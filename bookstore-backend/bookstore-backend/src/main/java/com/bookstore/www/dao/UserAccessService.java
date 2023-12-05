package com.bookstore.www.dao;

import com.bookstore.www.entity.Userinfo;
import com.bookstore.www.entity.User;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.repository.UserRepository;
import com.bookstore.www.repository.UserinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository /*标记该类用于访问、操作数据库*/
public class UserAccessService {
    /*数据访问对象层，负责实现和数据库的交互，提供对数据库的持久化操作，比如插入、删除，接受服务层的调用*/
    @Autowired
    public UserAccessService(EntityManager entityManager, UserRepository userRepository, UserinfoRepository userinfoRepository) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.userinfoRepository = userinfoRepository;
    }
    private EntityManager entityManager;
    private UserRepository userRepository;
    private UserinfoRepository userinfoRepository;
    public boolean checkUser(String username, String password){
        try {
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
            query.setParameter("username", username);
            User user = (User) query.getSingleResult();
            return user.getPassword().equals(password);
        } catch (NoResultException e) {
            return false;
        }
    }

    public Userinfo getInfo(String username){
        try {
            TypedQuery<Userinfo> query = entityManager.createQuery("SELECT u FROM Userinfo u WHERE u.username = :username", Userinfo.class);
            query.setParameter("username", username);
            Userinfo userinfo = query.getSingleResult();
            return userinfo;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Userinfo> selectAllUsers() {
        return userinfoRepository.findAll();
    }

    public boolean checkAdmin(String id) {
        try {
            if(id.isEmpty()) return false;
            Optional<User> userOptional = userRepository.findById(UUID.fromString(id));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return user.getRole();
            }
        } catch (Exception e) {
        }
        return false;
    }

    public Msg banUser(String userId) {
        try {
            Optional<Userinfo> userinfoOptional = userinfoRepository.findById(UUID.fromString(userId));
            if (userinfoOptional.isPresent()) {
                Userinfo userinfo = userinfoOptional.get();
                userinfo.setStatus(0);
                userinfoRepository.save(userinfo);
                return new Msg("success", null);
            }
            return new Msg("User not found", null);
        } catch (Exception e) {
            return new Msg("Error banning user", null);
        }
    }

    public Msg unbanUser(String userId) {
        try {
            Optional<Userinfo> userinfoOptional = userinfoRepository.findById(UUID.fromString(userId));
            if (userinfoOptional.isPresent()) {
                Userinfo userinfo = userinfoOptional.get();
                userinfo.setStatus(1);
                userinfoRepository.save(userinfo);
                return new Msg("success", null);
            }
            return new Msg("User not found", null);
        } catch (Exception e) {
            return new Msg("Error unbanning user", null);
        }
    }

    public Msg getUserById(String userId) {
        try {
            Optional<Userinfo> userinfoOptional = userinfoRepository.findById(UUID.fromString(userId));
            if(userinfoOptional.isPresent()){
                Userinfo userinfo = userinfoOptional.get();
                return new Msg("success", userinfo);
            }else{
                return new Msg("User not found", null);
            }
        } catch (Exception e){
            System.out.println(e);
            return new Msg("Error happened", null);
        }
    }

    public Msg register(User newuser, Userinfo newUserinfo) {
        // Check if email or username already exists
        boolean emailExists = userRepository.existsByEmail(newuser.getEmail());
        boolean usernameExists = userRepository.existsByUsername(newuser.getUsername());

        if (emailExists || usernameExists) {
            return new Msg("failed", null);
        }

        try {
            userRepository.save(newuser);
            userinfoRepository.save(newUserinfo);
            return new Msg("success", null);
        } catch (Exception e) {
            return new Msg("failed", null);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor=Exception.class)
    public User getUserDetailsById(UUID user_id) throws Exception{
        Optional<User> user = userRepository.findById(user_id);
        if(user.isPresent()){
            return user.get();
        }
        else{
            return null;
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor=Exception.class)
    public Msg updateUser(User user) throws Exception{
        //int i = 0;
        userRepository.save(user);
        return new Msg("success", null);
    }
}
