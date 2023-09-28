package com.bookstore.www.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "orderitems")
public class Orderitem {

    @Id
    @Setter @Getter
    @Column(name = "item_id")
    @JsonProperty("item_id")
    private UUID item_id;

    @Setter @Getter
    @Column(name = "orderbelong")
    @JsonProperty("orderbelong")
    private UUID orderbelong;

    @Setter @Getter
    @Column(name = "book_id")
    @JsonProperty("book_id")
    private UUID book_id;

    @Setter @Getter
    @Column(name = "quantity")
    @JsonProperty("quantity")
    private int quantity;

    @Setter @Getter
    @Column(name = "total_price")
    @JsonProperty("total_price")
    private double total_price;

    @ManyToOne
    @JoinColumn(name="book_id", insertable = false, updatable = false)
    private Book book1;

    @ManyToOne
    @JoinColumn(name="orderbelong", insertable = false, updatable = false)
    private Order order;

    public Orderitem(@JsonProperty("item_id") UUID item_id,
                 @JsonProperty("orderbelong") UUID orderbelong,
                 @JsonProperty("book_id") UUID book_id,
                 @JsonProperty("quantity") int quantity,
                 @JsonProperty("total_price") double total_price) {
        this.item_id = item_id;
        this.orderbelong = orderbelong;
        this.book_id = book_id;
        this.quantity = quantity;
        this.total_price = total_price;
    }

    public Orderitem() {
    }
}
