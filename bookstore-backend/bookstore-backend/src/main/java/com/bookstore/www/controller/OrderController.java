package com.bookstore.www.controller;

import com.bookstore.www.entity.Order;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping
    public Msg getAllOrders(@RequestParam("admin_id") String admin_id)
    {
        if(checkAdmin(admin_id)) {
            List<Order> result = orderService.getAllOrders();
            return new Msg("success", result);
        }else{
            return new Msg("admin_id is not valid!", null);
        }
    }

    @GetMapping("/me")
    public Msg getMyOrders(@RequestParam("user_id") String user_id){
        return orderService.getOrderById(user_id);
    }
    @PostMapping
    public Msg addNewItemToOrder(@RequestBody Order order){
        return orderService.purchaseNewItem(order);
    }

    public boolean checkAdmin(String id){
        boolean result = orderService.checkAdmin(id);
        return result;
    }

    @DeleteMapping("/{order_id}")
    public void deleteOrder(@PathVariable UUID order_id){
        orderService.deleteOrder(order_id);
    }
}



/*
*
* List.of(
            new Order(
                UUID.fromString("ed6fbe0f-116a-45c1-a661-8426343d0e59"),
                new ArrayList<>(
                    List.of(
                            new Order.bookAndAmount(UUID.fromString("0a72a144-1d1a-4e9e-9da4-9248d3952ba8"), "2"),
                            new Order.bookAndAmount(UUID.fromString("a256483b-19c3-4379-99a8-4b025486c546"), "1")
                    )
                )
            ),
            new Order(
                UUID.fromString("1b2c5f1b-7f77-3c93-17c4-0ead30a52351"),
                new ArrayList<>(
                        List.of(
                                new Order.bookAndAmount(UUID.fromString("0a72a144-1d1a-4e9e-9da4-9248d3952ba8"), "2"),
                                new Order.bookAndAmount(UUID.fromString("b940b0e9-4be3-fe9f-3211-4f99ece619c7"), "1")
                        )
                )
            )
        );*/
