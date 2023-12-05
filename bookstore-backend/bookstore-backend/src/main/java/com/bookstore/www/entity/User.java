package com.bookstore.www.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    /*实体层
    *定义了应用中的数据模型或对象的结构、行为，与数据库表对应
    */
    @Id
    @Column(name = "id")
    @Setter @Getter
    private UUID id;
    @Column(name = "email")
    @Setter @Getter
    private String email;
    @Column(name = "username")
    @Setter @Getter
    private String username;
    @Column(name = "password")
    @Setter @Getter
    private String password;
    @Column(name = "image")
    @Setter @Getter
    private String image;
    @Column(name = "role")
    @Setter @Getter
    private Boolean role;

    @Column(name = "cost")
    @Setter @Getter
    private double cost;

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL)
    private List<Order> orders;


    @OneToOne
    @JoinColumn(name="id")
    private Userinfo userinfo;

    public User(UUID id, String email, String username, String password, String image, Boolean role, double cost) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.image = image;
        this.role = role;
        this.cost = cost;
    }

    public User() {

    }


}
