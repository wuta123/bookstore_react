package com.bookstore.www.repository;

import com.bookstore.www.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

}

