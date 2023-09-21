package com.bookstore.www.controller;

import com.bookstore.www.service.BookService;
import com.bookstore.www.entity.Book;
import com.bookstore.www.msg.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("books")
public class BookController {
    static public class BookRequestDTO {
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setBook(Book book) {
            this.book = book;
        }

        public Book getBook() {
            return book;
        }

        private String id;
        private Book book;

        public BookRequestDTO(String id, Book book) {
            this.id = id;
            this.book = book;
        }

        // 添加构造函数、getter和setter方法
    }
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @GetMapping
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/getbyid")
    public Msg getBookById(@RequestParam("book_id") String book_id){
        return bookService.getBookById(book_id);
    }

    @RequestMapping("/add")
    public Msg addNewBook(@RequestBody BookRequestDTO requestDTO) {
        String id = requestDTO.getId();
        Book book = requestDTO.getBook();
        System.out.println("Add new book");
        System.out.println("Userid = "+ id);
        System.out.println("BookInfo ="+book.getBook_id()+","+book.getTitle());
        if(checkAdmin(id)){
            bookService.insertNewBook(book);
            return new Msg("成功添加到了书籍数据库！", null);
        }else{
            return new Msg("非法用户，拒绝操作", null);
        }
    }


    @RequestMapping("/modify")
    public Msg modifyBook(@RequestBody BookRequestDTO requestDTO){
        String id = requestDTO.getId();
        if(checkAdmin(id)){
            Book book = requestDTO.getBook();
            System.out.println("Admin: "+id+" modify a book");
            return bookService.modifyBook(book);
        }else{
            return new Msg("admin id is not valid", null);
        }

    }

    @RequestMapping("/delete")
    public Msg deleteBook(@RequestParam String id,@RequestParam String book_id) {
        if(checkAdmin(id)){
            if(bookService.deleteBook(book_id) == 1)
                return new Msg("已经删除！", null);
            else
                return new Msg("删除的书籍在数据库中不存在！", null);
        }else{
            return new Msg("非法用户，拒绝操作", null);
        }
    }

    public boolean checkAdmin(String id){
        return bookService.checkAdmin(id);
    }
}