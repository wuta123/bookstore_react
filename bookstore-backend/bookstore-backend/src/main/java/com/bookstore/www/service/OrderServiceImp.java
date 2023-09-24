package com.bookstore.www.service;

import com.bookstore.www.dao.OrderAccessService;
import com.bookstore.www.entity.Order;
import com.bookstore.www.msg.Msg;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImp implements OrderService {
    private final OrderAccessService orderAccessService;

    public OrderServiceImp(OrderAccessService orderAccessService) {
        this.orderAccessService = orderAccessService;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderAccessService.selectAllOrders();
    }

    @Override
    public Msg purchaseNewItem(Order order) {
        return orderAccessService.purchaseItem(order);
    }

    @Override
    public void deleteOrder(UUID order_id) {
        orderAccessService.deleteOrder(order_id);
    }

    @Override
    public boolean checkAdmin(String id) {
        return orderAccessService.checkAdmin(id);
    }

    @Override
    public Msg getOrderById(String userId) {
        return orderAccessService.getOrderById(userId);
    }
}
