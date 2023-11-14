package com.bookstore.www.dao;

import com.bookstore.www.entity.Cart;
import com.bookstore.www.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CartAccessService {

    private final CartRepository cartRepository;

    @Autowired
    public CartAccessService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<Cart> selectAllCarts() {
        return cartRepository.findAll();
    }

    public Cart insertItem(Cart newCart) {
        Cart existingCart = cartRepository.findByUser_idAndBook_id(newCart.getUser_id(), newCart.getBook_id());

        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + newCart.getQuantity());
            existingCart.setTotal_price(existingCart.getTotal_price() + newCart.getTotal_price());
            return cartRepository.save(existingCart);
        } else {
            newCart.setCart_id(UUID.randomUUID());
            return cartRepository.save(newCart);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public void deleteCart(UUID cart_id) {
        cartRepository.deleteById(cart_id);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Cart getCartDetailsByCartId(UUID cart_id) {
        Optional<Cart> cart = cartRepository.findById(cart_id);
        if(cart.isPresent())
            return cart.get();
        else return null;
    }
}
