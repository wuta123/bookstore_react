package com.bookstore.www.dao;

import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.Order;
import com.bookstore.www.entity.User;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.repository.BookRepository;
import com.bookstore.www.repository.OrderRepository;
import com.bookstore.www.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderAccessService {

    @Autowired
    public OrderAccessService(JdbcTemplate jdbcTemplate,
                              UserRepository userRepository,
                              OrderRepository orderRepository,
                              BookRepository bookRepository
                              ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    public final JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;
    private OrderRepository orderRepository;

    private BookRepository bookRepository;

    public List<Order> selectAllOrders() {
        return orderRepository.findAll();
    }

    public static RowMapper<Order> getOrderRowMapper() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("order_id");
            UUID order_id = UUID.fromString(idStr);
            idStr = resultSet.getString("user_id");
            UUID user_id = UUID.fromString(idStr);
            idStr = resultSet.getString("book_id");
            UUID book_id = UUID.fromString(idStr);
            int quantity = resultSet.getInt("quantity");
            double total_price = resultSet.getDouble("total_price");
            Timestamp purchase_time = resultSet.getTimestamp("purchase_time");
            return new Order(order_id, user_id, book_id, quantity, total_price,purchase_time);
        };
    }

    public Msg purchaseItem(Order newOrder) {
        System.out.println("New Order: " + newOrder);

        // 1. 使用bookRepository到book表中查找book_id === newOrder.book_id的行
        Book book = bookRepository.findById(newOrder.getBook_id()).orElse(null);

        if (book != null) {
            // 2. 检查该行的remain是否 >= newOrder.quantity
            if (book.getRemain() >= newOrder.getQuantity()) {
                // 3. 更新book表中该行的sold列值为sold+newOrder.quantity
                book.setSold(book.getSold() + newOrder.getQuantity());
                book.setRemain(book.getRemain() - newOrder.getQuantity());
                bookRepository.save(book);

                // 4. 使用userRepository到users表中查找id列值为newOrder.user_id的行，将该行的cost值变为cost+newOrder.total_price
                Optional<User> userOptional = userRepository.findById(newOrder.getUser_id());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setCost(user.getCost() + newOrder.getTotal_price());
                    userRepository.save(user);

                    // 插入新订单到orders表中
                    String sql = "INSERT INTO orders (user_id, book_id, quantity, total_price) VALUES (?, ?, ?, ?)";
                    int update = jdbcTemplate.update(
                            sql,
                            newOrder.getUser_id(),
                            newOrder.getBook_id(),
                            newOrder.getQuantity(),
                            newOrder.getTotal_price()
                    );

                    System.out.println("Insert Result: " + update);

                    if (update > 0) {
                        return new Msg("success", null);
                    }
                }
            }
        }

        return new Msg("failed", null);
    }



    public void deleteOrder(UUID order_id) {
        System.out.println("To be deleted order:" + order_id);
        String sql = "DELETE FROM orders WHERE order_id = ?";
        int update = jdbcTemplate.update(sql, order_id);
        System.out.println("Delete Result: " + update);
    }

    public boolean checkAdmin(String id) {
        try {
            if(id.isEmpty()) return false;
            Optional<User> userOptional = userRepository.findById(UUID.fromString(id));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return user.getRole();
            }
        } catch (Exception e) {
        }
        return false;
    }

    public Msg getOrderById(String user_id){
        String sql = "" +
                "SELECT " +
                " order_id, " +
                " user_id, " +
                " book_id, " +
                " quantity, " +
                " total_price, "+
                " purchase_time "+
                "FROM orders WHERE user_id=?";
        List<Order> orders = jdbcTemplate.query(sql,getOrderRowMapper(),UUID.fromString(user_id));
        return new Msg("success", orders);
    }
}

