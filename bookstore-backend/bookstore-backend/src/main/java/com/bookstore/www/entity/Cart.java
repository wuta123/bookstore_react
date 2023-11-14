package com.bookstore.www.entity;

import com.bookstore.www.entity.Book;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @Setter @Getter
    @Column(name = "cart_id")
    private UUID cart_id;
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

    @ManyToOne
    @JoinColumn(name="book_id", insertable = false, updatable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private User user1;

    public Cart(@JsonProperty("cart_id") UUID cart_id,
                @JsonProperty("user_id") UUID user_id,
                @JsonProperty("book_id") UUID book_id,
                @JsonProperty("quantity") int quantity,
                @JsonProperty("total_price") double total_price
                ){
        this.cart_id = cart_id;
        this.user_id = user_id;
        this.book_id = book_id;
        this.quantity = quantity;
        this.total_price = total_price;
    }

    public Cart(){

    }

}
