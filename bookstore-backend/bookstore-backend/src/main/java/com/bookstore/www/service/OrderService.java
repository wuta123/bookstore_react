package com.bookstore.www.service;
import com.bookstore.www.entity.Order;
import com.bookstore.www.dao.OrderAccessService;
import com.bookstore.www.entity.Orderitem;
import com.bookstore.www.msg.Msg;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    public List<Order> getAllOrders();

    public Msg purchaseNewItem(Order order, Orderitem item);

    Msg purchaseItemList(UUID order_id, UUID user_id, List<Map<String, String>> itemList);

    public void deleteOrder(UUID order_id);

    public boolean checkAdmin(String id);

    public JsonNode getOrderById(String userId);

    public JsonNode getOrderByOrderId(String orderId);
}

