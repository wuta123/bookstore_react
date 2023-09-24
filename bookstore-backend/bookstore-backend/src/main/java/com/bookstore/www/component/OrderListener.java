package com.bookstore.www.component;

import com.bookstore.www.entity.Order;
import com.bookstore.www.msg.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import com.bookstore.www.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderListener {
    private OrderService orderService;
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public OrderListener(OrderService orderService, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
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
        Msg result = orderService.purchaseNewItem(new Order(null,  user_id, book_id, quantity, total_price, null));
        System.out.println("插入过程结束");
        if(result.getMsg().equals("success") || result.getMsg().equals("successful"))
            System.out.println("订单：" + record.key() + "成功插入数据库");
    }
}
