package com.bookstore.www.service;
import com.bookstore.www.entity.Order;
import com.bookstore.www.dao.OrderAccessService;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    public List<Order> getAllOrders();

    public Msg purchaseNewItem(Order order);

    public void deleteOrder(UUID order_id);

    public boolean checkAdmin(String id);

    public Msg getOrderById(String userId);
}

