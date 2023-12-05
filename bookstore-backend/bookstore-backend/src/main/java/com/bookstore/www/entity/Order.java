package com.bookstore.www.entity;

import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Setter @Getter
    @Column(name = "order_id")
    @JsonProperty("order_id")
    private UUID order_id;

    @Setter @Getter
    @Column(name = "user_id")
    @JsonProperty("user_id")
    private UUID user_id;

    @Setter @Getter
    @Column(name = "purchase_time")
    @JsonProperty("purchase_time")
    private Timestamp purchase_time;

    @ManyToOne
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private User user1;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Orderitem> orderitemList;


    public Order(@JsonProperty("order_id") UUID order_id,
                 @JsonProperty("user_id") UUID user_id,
                 @JsonProperty("purchase_time") Timestamp purchase_time) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.purchase_time = purchase_time;
    }

    public Order() {
    }
}
