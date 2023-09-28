package com.bookstore.www.dao;
import com.bookstore.www.entity.*;
import com.bookstore.www.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bookstore.www.msg.Msg;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class OrderAccessService {
    @Autowired
    public OrderAccessService(JdbcTemplate jdbcTemplate,
                              UserRepository userRepository,
                              OrderRepository orderRepository,
                              ObjectMapper objectMapper,
                              BookRepository bookRepository,
                              OrderitemRepository orderitemRepository,
                              OrderitemAccessService orderitemAccessService, CartRepository cartRepository
                              ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.bookRepository = bookRepository;
        this.orderitemRepository = orderitemRepository;
        this.orderitemAccessService = orderitemAccessService;
        this.cartRepository = cartRepository;
    }

    public final JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;
    private OrderRepository orderRepository;

    private ObjectMapper objectMapper;

    private final OrderitemRepository orderitemRepository;
    private BookRepository bookRepository;

    private OrderitemAccessService orderitemAccessService;

    private CartRepository cartRepository;

    public List<Order> selectAllOrders() {
        return orderRepository.findAll();
    }

    public Msg purchaseItem(Order newOrder) {
        System.out.println("New Order: " + newOrder);
        Optional<Order> order = orderRepository.findById(newOrder.getOrder_id());
        if(!order.isPresent()){
            String sql = "INSERT INTO orders (order_id, user_id) VALUES (?, ?)";
            int update = jdbcTemplate.update(
                    sql,
                    newOrder.getOrder_id(),
                    newOrder.getUser_id()
            );
            System.out.println("Insert Result: " + (update));

            if (update > 0) {
                return new Msg("success", null);
            }
        }

        return new Msg("success", null);
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

    public JsonNode getOrderById(String user_id){
        String sql = "" +
                "SELECT " +
                " order_id, " +
                " user_id, " +
                " purchase_time "+
                "FROM orders WHERE user_id=?";
        List<JsonNode> orders = jdbcTemplate.query(sql,getOrderRowMapper(),UUID.fromString(user_id));
        ArrayNode myOrderList = objectMapper.createArrayNode();
        ObjectNode myOrders = objectMapper.createObjectNode();
        for(JsonNode node : orders){
            myOrderList.add(node);
        }
        myOrders.put("orders", myOrderList);
        myOrders.put("msg", "success");
        return myOrders;
    }

    public Order getOrderDetailsByOrderId(UUID order_id){
        Optional<Order> order = orderRepository.findById(order_id);
        if(order.isPresent())
            return order.get();
        else
            return null;
    }

    public RowMapper<JsonNode> getOrderRowMapper() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("order_id");
            UUID order_id = UUID.fromString(idStr);
            idStr = resultSet.getString("user_id");
            UUID user_id = UUID.fromString(idStr);
            Timestamp purchase_time = resultSet.getTimestamp("purchase_time");

            //idStr = resultSet.getString("book_id");
            //UUID book_id = UUID.fromString(idStr);
            //int quantity = resultSet.getInt("quantity");
            //double total_price = resultSet.getDouble("total_price");

            List<Orderitem> orderitems = orderitemRepository.findByOrderBelong(order_id);

            ObjectNode fullOrder = objectMapper.createObjectNode();
            ArrayNode itemArray = objectMapper.createArrayNode();
            for(Orderitem item: orderitems){
                ObjectNode tmp = objectMapper.createObjectNode();
                tmp.put("book_id", item.getBook_id().toString());
                tmp.put("item_id", item.getItem_id().toString());
                tmp.put("quantity", item.getQuantity());
                tmp.put("total_price", item.getTotal_price());
                itemArray.add(tmp);
            }
            fullOrder.put("order_id", order_id.toString());
            fullOrder.put("user_id", user_id.toString());
            fullOrder.put("purchase_time", purchase_time.toString());
            fullOrder.put("orderitems", itemArray);

            JsonNode result = fullOrder;
            return result;
        };
    }

    public JsonNode getOrderByOrderId(String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));
        ObjectNode fullOrder = objectMapper.createObjectNode();
        if(order.isPresent()){
            Order theOrder = order.get();
            ArrayNode itemArray = objectMapper.createArrayNode();
            List<Orderitem> orderitems = orderitemRepository.findByOrderBelong(UUID.fromString(orderId));
            for(Orderitem item: orderitems){
                ObjectNode tmp = objectMapper.createObjectNode();
                tmp.put("book_id", item.getBook_id().toString());
                tmp.put("item_id", item.getItem_id().toString());
                tmp.put("quantity", item.getQuantity());
                tmp.put("total_price", item.getTotal_price());
                itemArray.add(tmp);
            }
            fullOrder.put("order_id", theOrder.getOrder_id().toString());
            fullOrder.put("user_id", theOrder.getUser_id().toString());
            fullOrder.put("purchase_time", theOrder.getPurchase_time().toString());
            fullOrder.put("orderitems", itemArray);
            fullOrder.put("msg", "success");

            JsonNode result = fullOrder;
            return result;
        }
        else{
            fullOrder.put("msg", "failed");
            return fullOrder;
        }
    }
}

