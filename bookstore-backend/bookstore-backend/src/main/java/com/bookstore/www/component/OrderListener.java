package com.bookstore.www.component;

import com.bookstore.www.entity.Order;
import com.bookstore.www.entity.Orderitem;
import com.bookstore.www.msg.Msg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import com.bookstore.www.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderListener {
    private OrderService orderService;
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;
    @Autowired
    public OrderListener(OrderService orderService, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "buyQueue", groupId = "group_buy_queue")
    public void buyQueueListener(ConsumerRecord<String, String> record){
        System.out.println("OrderListener：接收到新的订单："+record.key());
        String mapString = record.value().substring(2, record.value().length()-2);
        String[] keyValuePairs = mapString.split(",");
        Map<String, String> params = new HashMap<>();
        for(String pair: keyValuePairs){
            String[] entry = pair.split("=");
            String key = entry[0].trim();
            String value = entry[1].trim();
            params.put(key, value);
        }
        UUID book_id = UUID.fromString(params.get("book_id"));
        UUID user_id = UUID.fromString(params.get("user_id"));
        int quantity = Integer.parseInt(params.get("quantity"));
        double total_price = Double.parseDouble(params.get("total_price"));
        System.out.println("成功解析订单信息。");
        Msg result = orderService.purchaseNewItem(
                new Order(UUID.fromString(record.key()), user_id, null),
                new Orderitem(null, UUID.fromString(record.key()), book_id, quantity, total_price)
        );

        System.out.println("插入过程结束");
        if(result.getMsg().equals("success") || result.getMsg().equals("successful"))
            System.out.println("订单：" + record.key() + "成功插入数据库");
    }

    @KafkaListener(topics = "cartBuyQueue", groupId = "group_cart_buy_queue")
    public void cartBuyListener(ConsumerRecord<String, String> record){
        try {
            JsonNode cartArray = objectMapper.readTree(record.value());
            System.out.println("OrderListener：接收到新的订单："+record.key());
            System.out.println(record.value());
            // 将JsonNode对象转换为List<Map<String, String>>，其中每个Map代表一个JSON对象
            List<Map<String, String>> itemList = new ArrayList<>();

            if (cartArray.isArray()) {
                Iterator<JsonNode> elements = cartArray.elements();
                while (elements.hasNext()) {
                    JsonNode item = elements.next();
                    if (item.isObject()) {
                        // 将JSON对象转换为Map
                        Map<String, String> itemMap = objectMapper.convertValue(item, Map.class);
                        itemList.add(itemMap);
                    }
                }
            }
            UUID order_id = UUID.fromString(record.key());
            System.out.println("成功解析订单信息。");

            if(itemList.isEmpty())
                return;
            else{
                UUID user_id = UUID.fromString(itemList.get(0).get("user_id"));
                orderService.purchaseItemList(order_id, user_id, itemList);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
