package com.bookstore.www.service;
import com.bookstore.www.dao.CartAccessService;
import com.bookstore.www.entity.Cart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartService {
    public final CartAccessService cartAccessService;
    public CartService(CartAccessService cartAccessService) {
        this.cartAccessService = cartAccessService;
    }
    public List<Cart> getAllCarts(){
        return cartAccessService.selectAllCarts();
    }
    public void addNewItem(Cart cart){
            cartAccessService.insertItem(cart);
    }
    public void deleteCart(UUID cart_id) {
        cartAccessService.deleteCart(cart_id);
    }
}
