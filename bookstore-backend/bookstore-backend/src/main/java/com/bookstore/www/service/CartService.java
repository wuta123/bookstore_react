package com.bookstore.www.service;
import com.bookstore.www.dao.CartAccessService;
import com.bookstore.www.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface CartService {
    public List<Cart> getAllCarts();
    public void addNewItem(Cart cart);
    public void deleteCart(UUID cart_id);
}

