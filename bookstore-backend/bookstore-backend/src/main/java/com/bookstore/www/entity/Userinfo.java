package com.bookstore.www.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "userinfo")
public class Userinfo {
    @Id
    @Column(name = "user_id")
    @Setter @Getter
    private UUID id;
    @Column(name = "email")
    @Setter @Getter
    private String email;
    @Column(name = "username")
    @Setter @Getter
    private String username;
    @Column(name = "image")
    @Setter @Getter
    private String image;

    @Column(name = "status")
    @Setter @Getter
    private int status;

    @Column(name = "role")
    @Setter @Getter
    private boolean role;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user1;


    public Userinfo(UUID id, String email, String username, String image, int status, boolean role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.image = image;
        this.status = status;
        this.role = role;
    }

    public Userinfo() {

    }


}
