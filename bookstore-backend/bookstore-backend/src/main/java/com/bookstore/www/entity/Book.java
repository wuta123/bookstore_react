package com.bookstore.www.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @Column(name="book_id")
    @Setter @Getter
    private UUID book_id;
    @Setter @Getter
    @Column(name="title")
    private String title;
    @Setter @Getter
    @Column(name="price")
    private String price;
    @Setter @Getter
    @Column(name="description")
    private String description;
    @Setter @Getter
    @Column(name="author")
    private String author;
    @Setter @Getter
    @Column(name="type")
    private String type;
    @Setter @Getter
    @Column(name="image")
    private String image;

    @Setter @Getter
    @Column(name="remain")
    private int remain;

    @Setter @Getter
    @Column(name="sold")
    private int sold;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Cart> carts;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Order> orders;


    public Book(UUID book_id, String title, String price, String description, String author, String type, String image, int remain, int sold) {
        this.book_id = book_id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.author = author;
        this.type = type;
        this.image = image;
        this.sold = sold;
        this.remain = remain;
    }

    public Book() {

    }
}
