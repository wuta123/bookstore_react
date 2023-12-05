package com.bookstore.www.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import jakarta.persistence.*;

@Document(collection = "cart")
public class Cart {

    @Id
    private UUID cart_id;

    private UUID user_id;

    public UUID getCart_id() {
        return cart_id;
    }

    public void setCart_id(UUID cart_id) {
        this.cart_id = cart_id;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public UUID getBook_id() {
        return book_id;
    }

    public void setBook_id(UUID book_id) {
        this.book_id = book_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    private UUID book_id;

    private int quantity;

    private double total_price;


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
