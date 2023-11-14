package com.bookstore.www.repository;

import com.bookstore.www.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.UUID;

public interface CartRepository extends MongoRepository<Cart, UUID> {
    @Query("{ 'user_id' : ?0, 'book_id' : ?1 }")
    Cart findByUser_idAndBook_id(UUID user_id, UUID book_id);
}

