package com.bookstore.www.service;

import com.bookstore.www.dao.BookAccessService;
import com.bookstore.www.entity.Book;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    public BookService(BookAccessService bookAccessService) {
        this.bookAccessService = bookAccessService;
    }

    private final BookAccessService bookAccessService;
    public List<Book> getAllBooks(){
        return bookAccessService.selectAllBooks();
    }

    public boolean checkAdmin(String id){
        return bookAccessService.checkAdmin(id);
    }

    public void insertNewBook(Book newbook) {
        bookAccessService.insertNewBook(newbook);
    }

    public Msg modifyBook(Book newbook) {
        System.out.println(newbook.getBook_id());
        return bookAccessService.modifyBook(newbook);
    }

    public int deleteBook(String bookid) {
        return bookAccessService.deleteBook(bookid);
    }

    public Msg getBookById(String bookId) {
        return bookAccessService.getBookById(bookId);
    }

}
