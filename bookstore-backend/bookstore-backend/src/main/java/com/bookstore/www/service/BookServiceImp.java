package com.bookstore.www.service;

import com.bookstore.www.dao.BookAccessService;
import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.BookType;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImp implements BookService {
    public BookServiceImp(BookAccessService bookAccessService) {
        this.bookAccessService = bookAccessService;
    }

    private final BookAccessService bookAccessService;

    @Override
    public List<Book> getAllBooks() {
        return bookAccessService.selectAllBooks();
    }

    @Override
    public boolean checkAdmin(String id) {
        return bookAccessService.checkAdmin(id);
    }

    @Override
    public void insertNewBook(Book newbook) {
        bookAccessService.insertNewBook(newbook);
    }

    @Override
    public Msg modifyBook(Book newbook) {
        System.out.println(newbook.getBook_id());
        return bookAccessService.modifyBook(newbook);
    }

    @Override
    public int deleteBook(String bookid) {
        return bookAccessService.deleteBook(bookid);
    }

    @Override
    public Msg getBookById(String bookId) {
        return bookAccessService.getBookById(bookId);
    }

    @Override
    public List<Book> getBookByRelatedType(String type) {
        return bookAccessService.findBooksByTypeRelated(type);
    }

    @Override
    public void test() {
        bookAccessService.test();
    }

}
