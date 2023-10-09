package com.bookstore.www.dao;

import com.alibaba.fastjson.JSON;
import com.bookstore.www.entity.Book;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.repository.BookRepository;
import com.bookstore.www.repository.UserRepository;
import com.bookstore.www.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class BookAccessService {
    @Autowired
    public BookAccessService(JdbcTemplate jdbcTemplate,
                             UserRepository userRepository,
                             BookRepository bookRepository,
                             EntityManager entityManager,
                             RedisTemplate redisTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.entityManager = entityManager;
        this.redisTemplate = redisTemplate;
    }
    public UserRepository userRepository;
    public BookRepository bookRepository;
    public EntityManager entityManager;
    public final JdbcTemplate jdbcTemplate;
    public final RedisTemplate redisTemplate;

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
                try {
                    redisTemplate.opsForValue().set("book" + book.getBook_id().toString(), JSON.toJSONString(book));
                    redisTemplate.opsForValue().set("bookDetail" + book.getBook_id().toString(), JSON.toJSONString(book));
                    System.out.println("进行书籍信息的修改，更新书籍：" + book.getTitle() + "在redis库缓存当中的信息");
                } catch (RedisConnectionFailureException e) {
                    System.err.println("无法链接到redis服务器，因此本次修改没有被缓存.");
                }
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

        try {
            redisTemplate.delete("book"+bid);
            redisTemplate.delete("bookDetail"+bid);
            System.out.println("在redis缓存中删除书籍："+ bid);
        } catch (RedisConnectionFailureException e) {
            System.err.println("无法链接到redis服务器，因此本次删除没有被缓存.");
        }
        return 1;
    }

    public Msg getBookById(String bookId) {
        System.out.println("Get book by id: "+bookId);
        UUID book_id = UUID.fromString(bookId);
        String bookString;
        //TODO: TRY AND CATCH
        try {
            bookString = (String) redisTemplate.opsForValue().get("book" + bookId);
        }catch(RedisConnectionFailureException e){
            bookString = null;
            System.out.println("无法链接到redis服务器，因此本次获取书籍没有使用缓存");
        }

        if(bookString == null) {
            Optional<Book> bookOptional = bookRepository.findById(book_id);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                try {
                    redisTemplate.opsForValue().set("book" + bookId, JSON.toJSONString(book));
                }catch(RedisConnectionFailureException e){
                    System.out.println("无法链接到redis服务器，本次储存失败");
                }
                return new Msg("success", book);
            } else {
                return new Msg("Failed to find book", null);
            }
        }else{
            Book book = JSON.parseObject(bookString, Book.class);
            System.out.println("从redis库当中读取了书籍："+book.getTitle());
            return new Msg("success", book);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor=Exception.class)
    public Book getBookDetailById(UUID book_id) throws Exception{
        String bookString;
        //TODO: TRY AND CATCH
        try {
            bookString = (String) redisTemplate.opsForValue().get("bookDetail"+book_id.toString());
        }catch(RedisConnectionFailureException e){
            bookString = null;
            System.out.println("无法链接到redis服务器，因此本次获取书籍没有使用缓存");
        }
        if(bookString == null) {
            Optional<Book> bookOptional = bookRepository.findById(book_id);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                try {
                    redisTemplate.opsForValue().set("bookDetail" + book_id.toString(), JSON.toJSONString(book));
                }catch (RedisConnectionFailureException e){
                    System.out.println("无法链接到redis服务器");
                }
                return book;
            }
            else
                return null;
        }else{
            Book book = JSON.parseObject(bookString, Book.class);
            System.out.println("从redis库当中读取了书籍："+book.getTitle());
            return book;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor=Exception.class)
    public Msg updateBook(Book book) throws Exception{
        //TODO: TRY AND CATCH
        try {
            redisTemplate.opsForValue().set("book" + book.getBook_id().toString(), JSON.toJSONString(book));
            redisTemplate.opsForValue().set("bookDetail" + book.getBook_id().toString(), JSON.toJSONString(book));
            System.out.println("进行书籍信息的修改，更新书籍：" + book.getTitle() + "在redis库缓存当中的信息");
        }catch(RedisConnectionFailureException e){
            System.out.println("无法链接到Redis服务器，本次更新不可被记录");
        }
        bookRepository.save(book);
        return new Msg("success", null);
    }

}
