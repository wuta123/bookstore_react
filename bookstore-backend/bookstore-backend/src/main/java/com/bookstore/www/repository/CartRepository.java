package com.bookstore.www.repository;

import com.bookstore.www.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

}

