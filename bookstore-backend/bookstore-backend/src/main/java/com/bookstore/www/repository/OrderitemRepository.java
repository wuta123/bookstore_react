package com.bookstore.www.repository;

import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.Orderitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderitemRepository extends JpaRepository<Orderitem, UUID> {

    @Query("SELECT o FROM Orderitem o WHERE o.orderbelong = :orderbelong")
    List<Orderitem> findByOrderBelong(UUID orderbelong);
}

