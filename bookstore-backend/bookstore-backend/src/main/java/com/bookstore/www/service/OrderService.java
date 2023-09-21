package com.bookstore.www.service;
import com.bookstore.www.entity.Order;
import com.bookstore.www.dao.OrderAccessService;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderAccessService orderAccessService;
    public OrderService(OrderAccessService orderAccessService) {
        this.orderAccessService = orderAccessService;
    }
    public List<Order> getAllOrders(){
        return orderAccessService.selectAllOrders();
    }

    public Msg purchaseNewItem(Order order){
        return orderAccessService.purchaseItem(order);
    }

    public void deleteOrder(UUID order_id) {
        orderAccessService.deleteOrder(order_id);
    }

    public boolean checkAdmin(String id) {
        return orderAccessService.checkAdmin(id);
    }

    public Msg getOrderById(String userId) {
        return orderAccessService.getOrderById(userId);
    }
}
