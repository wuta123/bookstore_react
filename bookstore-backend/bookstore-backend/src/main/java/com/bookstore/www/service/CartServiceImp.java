package com.bookstore.www.service;

import com.bookstore.www.dao.CartAccessService;
import com.bookstore.www.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImp implements CartService{

    public final CartAccessService cartAccessService;

    public CartServiceImp(CartAccessService cartAccessService) {
        this.cartAccessService = cartAccessService;
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartAccessService.selectAllCarts();
    }

    @Override
    public void addNewItem(Cart cart) {
        cartAccessService.insertItem(cart);
    }

    @Override
    public void deleteCart(UUID cart_id) {
        cartAccessService.deleteCart(cart_id);
    }
}
