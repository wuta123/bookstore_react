package com.bookstore.www.dao;

import com.bookstore.www.entity.Book;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.repository.BookRepository;
import com.bookstore.www.repository.UserRepository;
import com.bookstore.www.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class BookAccessService {
    @Autowired
    public BookAccessService(JdbcTemplate jdbcTemplate, UserRepository userRepository, BookRepository bookRepository, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.entityManager = entityManager;
    }
    @Autowired
    public UserRepository userRepository;
    public BookRepository bookRepository;
    public EntityManager entityManager;
    public final JdbcTemplate jdbcTemplate;
    public List<Book> selectAllBooks(){
        return bookRepository.findAll();
    }

    public boolean checkAdmin(String id) {
        try {
            Optional<User> userOptional = userRepository.findById(UUID.fromString(id));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return user.getRole();
            }
        } catch (Exception e) {
            // 处理查询出错的情况
            e.printStackTrace();
        }
        return false;
    }

    public static RowMapper<Book> getBookRowMapper() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("book_id");
            UUID book_id = UUID.fromString(idStr);
            String title = resultSet.getString("title");
            String price = resultSet.getString("price");
            String description = resultSet.getString("description");
            String author = resultSet.getString("author");
            String type = resultSet.getString("type");
            String image = resultSet.getString("image");
            int remain = resultSet.getInt("remain");
            int sold = resultSet.getInt("sold");
            return new Book(book_id, title, price, description, author, type, image, remain, sold);
        };
    }

    public void insertNewBook(Book newbook) {
        System.out.println("New Book: "+ newbook);

        String sql = ""+
                "INSERT INTO book (book_id, title, price, description, author, type, image, remain, sold) "+
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int update = jdbcTemplate.update(
                sql,
                newbook.getBook_id(),
                newbook.getTitle(),
                newbook.getPrice(),
                newbook.getDescription(),
                newbook.getAuthor(),
                newbook.getType(),
                newbook.getImage(),
                newbook.getRemain(),
                newbook.getSold()
        );
        System.out.println("Insert Result: "+update);
    }

    public Msg modifyBook(Book newbook) {
        System.out.println("Modify Book: " + newbook);

        try {
            Optional<Book> bookOptional = bookRepository.findById(newbook.getBook_id());
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                book.setTitle(newbook.getTitle());
                book.setPrice(newbook.getPrice());
                book.setDescription(newbook.getDescription());
                book.setAuthor(newbook.getAuthor());
                book.setType(newbook.getType());
                book.setImage(newbook.getImage());
                book.setRemain(newbook.getRemain());
                bookRepository.save(book);
                return new Msg("success", null);
            } else {
                return new Msg("failed", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("failed", null);
        }
    }


    public int deleteBook(String bid) {
        UUID book_id = UUID.fromString(bid);
        System.out.println("To be deleted book: " + book_id);
        String sql = "DELETE FROM book WHERE book_id = ?";
        int update = jdbcTemplate.update(sql, book_id);
        System.out.println("Delete Result: " + update);
        return 1;
    }

    public Msg getBookById(String bookId) {
        System.out.println("Get book by id: "+bookId);
        UUID book_id = UUID.fromString(bookId);
        Optional<Book> bookOptional = bookRepository.findById(book_id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            return new Msg("success", book);
        }else{
            return new Msg("Failed to find book", null);
        }
    }

    public Book getBookDetailById(UUID book_id){
        Optional<Book> book = bookRepository.findById(book_id);
        if(book.isPresent())
            return book.get();
        else
            return null;
    }

    public Msg updateBook(Book book){
        bookRepository.save(book);
        return new Msg("success", null);
    }

}
