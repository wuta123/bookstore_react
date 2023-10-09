package com.bookstore.www.controller;

import com.bookstore.www.entity.Order;
import com.bookstore.www.msg.Msg;
import com.bookstore.www.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;
    private final KafkaTemplate kafkaTemplate;
    @Autowired
    public OrderController(OrderService orderService, KafkaTemplate kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
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
    public JsonNode getMyOrders(@RequestParam("user_id") String user_id){
        JsonNode result = orderService.getOrderById(user_id);
        return result;
    }

    @GetMapping("/detail")
    public JsonNode getOrderDetailsByOrderId(@RequestParam("order_id") String order_id){
        JsonNode result = orderService.getOrderByOrderId(order_id);
        return result;
    }

    @PostMapping("/list")
    public Msg purchaseAListOfItems(@RequestBody JsonNode cartArray){
        String OrderId = UUID.randomUUID().toString();
        kafkaTemplate.send("cartBuyQueue", OrderId, cartArray);
        System.out.println("接受到一组下单请求");
        System.out.println(cartArray);
        return new Msg("success", OrderId);
    }

    @PostMapping
    public Msg addNewItemToOrder(@RequestBody Map<String, String> params){
        String OrderId = UUID.randomUUID().toString();
        kafkaTemplate.send("buyQueue", OrderId, params.toString());
        System.out.println("接收到用户下单，单号为："+OrderId+"，将对应下单信息发送给前端");
        return new Msg("success", OrderId);
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
