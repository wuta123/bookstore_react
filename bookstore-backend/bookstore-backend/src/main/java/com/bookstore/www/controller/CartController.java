package com.bookstore.www.controller;

import com.bookstore.www.service.CartService;
import com.bookstore.www.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("carts")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping
    public List<Cart> getAllCarts(){
        return cartService.getAllCarts();
    }
    @PostMapping
    public void addNewItemToCart(@RequestBody Cart cart){
        cartService.addNewItem(cart);
    }
    @DeleteMapping("/{cart_id}")
    public void deleteCart(@PathVariable UUID cart_id){
        cartService.deleteCart(cart_id);
    }
}


