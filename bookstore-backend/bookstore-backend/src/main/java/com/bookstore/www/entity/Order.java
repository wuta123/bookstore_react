package com.bookstore.www.entity;

import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Setter @Getter
    @Column(name = "order_id")
    private UUID order_id;

    @Setter @Getter
    @Column(name = "user_id")
    private UUID user_id;

    @Setter @Getter
    @Column(name = "book_id")
    private UUID book_id;

    @Setter @Getter
    @Column(name = "quantity")
    private int quantity;

    @Setter @Getter
    @Column(name = "total_price")
    private double total_price;

    @Setter @Getter
    @Column(name = "purchase_time")
    private Timestamp purchase_time;

    @ManyToOne
    @JoinColumn(name="book_id", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private User user1;


    public Order(@JsonProperty("order_id") UUID order_id,
                 @JsonProperty("user_id") UUID user_id,
                 @JsonProperty("book_id") UUID book_id,
                 @JsonProperty("quantity") int quantity,
                 @JsonProperty("total_price") double total_price,
                 @JsonProperty("purchase_time") Timestamp purchase_time) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.book_id = book_id;
        this.quantity = quantity;
        this.total_price = total_price;
        this.purchase_time = purchase_time;
    }

    public Order() {
    }
}
