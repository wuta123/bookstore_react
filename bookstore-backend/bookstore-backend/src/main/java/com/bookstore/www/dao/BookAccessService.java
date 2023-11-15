package com.bookstore.www.dao;

import com.alibaba.fastjson.JSON;
import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.BookType;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.repository.BookRepository;
import com.bookstore.www.repository.BookTypeRepository;
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

import java.util.*;

@Repository
@Transactional
public class BookAccessService {
    @Autowired
    public BookAccessService(JdbcTemplate jdbcTemplate,
                             UserRepository userRepository,
                             BookRepository bookRepository,
                             EntityManager entityManager,
                             RedisTemplate redisTemplate,
                             BookTypeRepository bookTypeRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.entityManager = entityManager;
        this.redisTemplate = redisTemplate;
        this.bookTypeRepository = bookTypeRepository;
    }
    public UserRepository userRepository;
    public BookRepository bookRepository;
    public EntityManager entityManager;

    public BookTypeRepository bookTypeRepository;
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

    public List<Book> findBooksByTypeRelated(String type){
        BookType bookType = bookTypeRepository.findBookTypeByNameLike(type);
        List<Book> result = new ArrayList<>();
        Set<UUID> ids = new HashSet<UUID>();
        if(bookType == null){
            return result;
        }else{
            if(bookType.getBook_ids() != null) {
                for (UUID id : bookType.getBook_ids()) {
                    ids.add(id);
                }
            }
        }

        List<BookType> l1 = bookTypeRepository.findRelatedBookTypes1(type);
        List<BookType> l2 = bookTypeRepository.findRelatedBookTypes2(type);

        for(BookType typename : l1){
            if(typename.getBook_ids() != null) {
                for (UUID id : typename.getBook_ids()) {
                    ids.add(id);
                }
            }
        }
        for(BookType typename : l2){
            if(typename.getBook_ids() != null) {
                for (UUID id : typename.getBook_ids()) {
                    ids.add(id);
                }
            }
        }

        for(UUID id : ids){
            try {
                result.add(this.getBookDetailById(id));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public void test(){
        List<BookType> blist = new ArrayList<>();
        blist.add(new BookType("小说")); //0
        blist.add(new BookType("推理小说"));
        blist.add(new BookType("悬疑小说"));
        blist.add(new BookType("恐怖小说"));
        blist.add(new BookType("爱情小说"));
        blist.add(new BookType("历史小说"));
        blist.add(new BookType("冒险小说"));
        blist.add(new BookType("战争小说"));
        blist.add(new BookType("幻想小说"));
        blist.add(new BookType("工具书籍")); //9
        blist.add(new BookType("百科全书"));
        blist.add(new BookType("词典"));
        blist.add(new BookType("手册"));
        blist.add(new BookType("戏剧"));//13
        blist.add(new BookType("喜剧"));
        blist.add(new BookType("悲剧"));
        blist.add(new BookType("历史剧"));
        blist.add(new BookType("名著"));//17
        blist.add(new BookType("文学名著"));
        blist.add(new BookType("历史名著"));
        blist.add(new BookType("古典名著"));

        blist.get(12).addBookID(UUID.fromString("911bc923-3f32-4843-ad7a-741a382614e4"));
        blist.get(0).addBookID(UUID.fromString("e65fd6f4-14f4-459c-9bc6-a05156378090"));
        blist.get(8).addBookID(UUID.fromString("aa518d23-08d7-41b2-9935-62badd84ee30"));
        blist.get(17).addBookID(UUID.fromString("b659fafa-f189-4e4a-a110-5be0a1bed710"));

        for(int a = 1; a < 9; a++){
            blist.get(0).addRelateBookType(blist.get(a));
            blist.get(a).addRelateBookType(blist.get(0));
        }

        for(int a = 10; a < 13; a++){
            blist.get(9).addRelateBookType(blist.get(a));
            blist.get(a).addRelateBookType(blist.get(9));
        }

        for(int a = 14; a < 17; a++){
            blist.get(13).addRelateBookType(blist.get(a));
            blist.get(a).addRelateBookType(blist.get(13));
        }

        for(int a = 18; a < 21; a++){
            blist.get(17).addRelateBookType(blist.get(a));
            blist.get(a).addRelateBookType(blist.get(17));
        }

        for(int a = 0; a < 21; a++){
            bookTypeRepository.save(blist.get(a));
        }

    }

}
