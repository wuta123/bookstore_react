package com.bookstore.www.service;

import com.bookstore.www.dao.*;
import com.bookstore.www.entity.*;
import com.bookstore.www.msg.Msg;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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


    //种类1，直接从商店页面直接购买的物品
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public Msg purchaseNewItem(Order order, Orderitem item) throws Exception {
        //1.检查User是否合法，如果不合法，直接中断。
        User u = userAccessService.getUserDetailsById(order.getUser_id());
        if(u == null)
            return new Msg("failed", null);
        //2.检查Book是否合法，如果不合法，直接中断。
        Book book = bookAccessService.getBookDetailById(item.getBook_id());
        if(book == null){
            return new Msg("failed", null);
        }
        //3.检查书籍存量和发起订单的请求量的区别，假如发起的订单请求量大于书籍库存量，直接中断，否则更新书籍的库存量和销量
        if (book.getRemain() >= item.getQuantity()) {
            //更新book表中该行的sold列值为sold+newOrder.quantity
            book.setSold(book.getSold() + item.getQuantity());
            book.setRemain(book.getRemain() - item.getQuantity());
            bookAccessService.updateBook(book);
        }else{
            return new Msg("failed", null);
        }
        //4.先插入order，因为orderitem和order通过外键关联，所以插入顺序不可变。
        Msg result1 = orderAccessService.purchaseItem(order);
        //5.插入新的orderitem
        Msg result2 = orderitemAccessService.purchaseOneItem(item);
        //6.假如两次插入的结果都是成功，返回“成功”信息给前台
        Msg returnRes = result1.getMsg().equals("success") && result2.getMsg().equals("success") ?
                new Msg("success", null) : new Msg("failed", null);
        //7.更新用户的消费额
        u.setCost(u.getCost() + item.getTotal_price());
        userAccessService.updateUser(u);
        return returnRes;
    }

    //种类2，从购物车内购买（包括从购物车内购买单个项目)
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public Msg purchaseItemList(UUID order_id, UUID user_id, List<Map<String, String>> itemList) throws Exception{
        //1.检查该用户是否合法
        User u = userAccessService.getUserDetailsById(user_id);
        if(u == null){
            return new Msg("failed", null);
        }
        //2.首先创建新订单，然后将购买的物品按顺序插入
        Order newOrder = new Order(order_id, user_id, null);
        orderAccessService.purchaseItem(newOrder);

        boolean success = true;
        //3.插入新的物品
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

            Msg res = orderitemAccessService.purchaseOneItem(item);
            if(!res.getMsg().equals("success")){
                success = false;
                break;
            }

            u.setCost(u.getCost() + item.getTotal_price());

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
