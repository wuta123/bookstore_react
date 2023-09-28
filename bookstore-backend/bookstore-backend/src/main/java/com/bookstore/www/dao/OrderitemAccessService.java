package com.bookstore.www.dao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bookstore.www.entity.Book;
import com.bookstore.www.entity.Order;
import com.bookstore.www.entity.Orderitem;
import com.bookstore.www.entity.User;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.repository.BookRepository;
import com.bookstore.www.repository.OrderRepository;
import com.bookstore.www.repository.OrderitemRepository;
import com.bookstore.www.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class OrderitemAccessService {

    @Autowired
    public OrderitemAccessService(JdbcTemplate jdbcTemplate,
                              ObjectMapper objectMapper,
                              OrderitemRepository orderitemRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.orderitemRepository = orderitemRepository;
    }

    public final JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;
    private OrderitemRepository orderitemRepository;

    public Msg purchaseOneItem(Orderitem item) {
        String sql2 = "INSERT INTO orderitems (orderbelong, book_id, quantity, total_price) VALUES (?, ?, ?, ?)";
        int update2 = jdbcTemplate.update(
                sql2,
                item.getOrderbelong(),
                item.getBook_id(),
                item.getQuantity(),
                item.getTotal_price()
        );

        System.out.println("Insert Result: " + (update2 & 1));

        if (update2 > 0) {
            return new Msg("success", null);
        }

        return new Msg("failed", null);
    }

    public Msg purchaseItemList(Orderitem[] itemList){
        boolean success = true;
        for(Orderitem item : itemList){
            Msg result = purchaseOneItem(item);
            if(result.getMsg() != "success"){
                success = false;
            }
        }
        String msg = success ? "success" : "failed";
        return new Msg(msg, null);
    }

    public void deleteOrderItemByItem_id(UUID item_id) {
        System.out.println("To be deleted orderitem:" + item_id);
        String sql = "DELETE FROM orderitems WHERE item_id = ?";
        int update = jdbcTemplate.update(sql, item_id);
        System.out.println("Delete Result: " + update);
    }

    public void deleteOrderItemByOrder_id(UUID order_id) {
        System.out.println("To be deleted orderitem:" + order_id);
        String sql = "DELETE FROM orderitems WHERE orderbelong = ?";
        int update = jdbcTemplate.update(sql, order_id);
        System.out.println("Delete Result: " + update);
    }

    public ArrayNode GetOrderItemArrayNodeByOrderId(String orderId) {
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

        return itemArray;
    }

    public Orderitem getOrderitemDetailsByItemId(UUID item_id){
        Optional<Orderitem> orderitem = orderitemRepository.findById(item_id);
        if(orderitem.isPresent())
            return orderitem.get();
        else
            return null;
    }
}

