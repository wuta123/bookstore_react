package com.bookstore.www.service;

import com.bookstore.www.dao.BookAccessService;
import com.bookstore.www.entity.Book;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BookService {
    public List<Book> getAllBooks();

    public boolean checkAdmin(String id);

    public void insertNewBook(Book newbook);

    public Msg modifyBook(Book newbook);

    public int deleteBook(String bookid);

    public Msg getBookById(String bookId);

}

