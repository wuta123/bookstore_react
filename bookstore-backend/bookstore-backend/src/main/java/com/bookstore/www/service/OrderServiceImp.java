package com.bookstore.www.service;

import com.bookstore.www.dao.*;
import com.bookstore.www.entity.*;
import com.bookstore.www.msg.Msg;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImp implements OrderService {
    private final OrderAccessService orderAccessService;
    private final OrderitemAccessService orderitemAccessService;

    private final CartAccessService cartAccessService;
    private final UserAccessService userAccessService;

    private final BookAccessService bookAccessService;

    @Autowired
    public OrderServiceImp(OrderAccessService orderAccessService, OrderitemAccessService orderitemAccessService, CartAccessService cartAccessService, UserAccessService userAccessService, BookAccessService bookAccessService) {
        this.orderAccessService = orderAccessService;
        this.orderitemAccessService = orderitemAccessService;
        this.cartAccessService = cartAccessService;
        this.userAccessService = userAccessService;
        this.bookAccessService = bookAccessService;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderAccessService.selectAllOrders();
    }

    @Override
    public Msg purchaseNewItem(Order order, Orderitem item) {
        User u = userAccessService.getUserDetailsById(order.getUser_id());
        if(u == null)
            return new Msg("failed", null);
        Book book = bookAccessService.getBookDetailById(item.getBook_id());
        if(book == null){
            return new Msg("failed", null);
        }

        if (book.getRemain() >= item.getQuantity()) {
            //更新book表中该行的sold列值为sold+newOrder.quantity
            book.setSold(book.getSold() + item.getQuantity());
            book.setRemain(book.getRemain() - item.getQuantity());
            bookAccessService.updateBook(book);
        }else{
            return new Msg("failed", null);
        }
        u.setCost(u.getCost() + item.getTotal_price());
        userAccessService.updateUser(u);
        Msg result1 = orderAccessService.purchaseItem(order);
        Msg result2 = orderitemAccessService.purchaseOneItem(item);
        Msg returnRes = result1.getMsg().equals("success") && result2.getMsg().equals("success") ?
                new Msg("success", null) : new Msg("failed", null);
        return returnRes;
    }

    @Override
    public Msg purchaseItemList(UUID order_id, UUID user_id, List<Map<String, String>> itemList) {
        Order newOrder = new Order(order_id, user_id, null);
        orderAccessService.purchaseItem(newOrder);
        User u = userAccessService.getUserDetailsById(user_id);
        if(u == null){
            return new Msg("failed", null);
        }
        boolean success = true;

        for (Map<String, String> itemMap : itemList) {
            UUID cart_id = UUID.fromString(itemMap.get("cart_id"));
            Cart c = cartAccessService.getCartDetailsByCartId(cart_id);
            if(c == null || !c.getUser_id().toString().equals(user_id.toString())){
                success = false;
                break;
            }

            Orderitem item = new Orderitem(null, newOrder.getOrder_id(), c.getBook_id(), c.getQuantity(), c.getTotal_price());
            Book book = bookAccessService.getBookDetailById(c.getBook_id());
            if(book == null){
                success=false;
                break;
            }

            if (book.getRemain() >= item.getQuantity()) {
                //更新book表中该行的sold列值为sold+newOrder.quantity
                book.setSold(book.getSold() + item.getQuantity());
                book.setRemain(book.getRemain() - item.getQuantity());
                bookAccessService.updateBook(book);
            }else{
                success=false;
                break;
            }
            u.setCost(u.getCost() + item.getTotal_price());

            Msg res = orderitemAccessService.purchaseOneItem(item);
            if(!res.getMsg().equals("success")){
                success = false;
                break;
            }
            cartAccessService.deleteCart(c.getCart_id());
        }
        userAccessService.updateUser(u);
        return new Msg(success ? "success": "failed", null);
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
    public JsonNode getOrderById(String userId) {

        return orderAccessService.getOrderById(userId);
    }

    @Override
    public JsonNode getOrderByOrderId(String orderId) {
        return orderAccessService.getOrderByOrderId(orderId);
    }
}
