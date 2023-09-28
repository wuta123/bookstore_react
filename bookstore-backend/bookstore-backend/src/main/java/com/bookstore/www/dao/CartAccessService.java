package com.bookstore.www.dao;

import com.bookstore.www.entity.Cart;
import com.bookstore.www.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class CartAccessService {
    @Autowired
    public CartAccessService(JdbcTemplate jdbcTemplate,
                             CartRepository cartRepository
                             )
    {
        this.jdbcTemplate = jdbcTemplate;
        this.cartRepository = cartRepository;
    }

    private CartRepository cartRepository;
    public final JdbcTemplate jdbcTemplate;

    public List<Cart> selectAllCarts() {
        return cartRepository.findAll();
    }

    public static RowMapper<Cart> getCartRowMapper() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("cart_id");
            UUID cart_id = UUID.fromString(idStr);
            idStr = resultSet.getString("user_id");
            UUID user_id = UUID.fromString(idStr);
            idStr = resultSet.getString("book_id");
            UUID book_id = UUID.fromString(idStr);
            int quantity = resultSet.getInt("quantity");
            double total_price = resultSet.getDouble("total_price");
            return new Cart(cart_id, user_id, book_id, quantity, total_price);
        };
    }

    public void insertItem(Cart newCart){  //后端接受到前端的发送请求，会将接受到的新购物车放在这里
        //首先查询
        String sqlQuery = "SELECT * FROM Cart WHERE user_id = ? AND book_id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                sqlQuery,
                newCart.getUser_id(),
                newCart.getBook_id()
        );

        List<Cart> existingCarts = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Cart cart = new Cart();
            cart.setCart_id((UUID) row.get("cart_id"));
            cart.setUser_id((UUID) row.get("user_id"));
            cart.setBook_id((UUID) row.get("book_id"));
            cart.setQuantity((int) row.get("quantity"));
            //由于是BigDecimal类型，需要多进行一次转换
            BigDecimal re = (BigDecimal) row.get("total_price");
            cart.setTotal_price(re.doubleValue());
            existingCarts.add(cart);
        }

        //如果存在，数量和价格叠加，如果不存在，新建条目插入。
        if(existingCarts.isEmpty()){
            String sqlInsert = "" +
                    "INSERT INTO cart (user_id, book_id, quantity, total_price) "+
                    "VALUES (?, ?, ?, ?)";
            int update = jdbcTemplate.update(sqlInsert,
                    newCart.getUser_id(),
                    newCart.getBook_id(),
                    newCart.getQuantity(),
                    newCart.getTotal_price()
                    );
            System.out.println("Insert Result: " + update);
        }else{
            Cart existingCart = existingCarts.get(0);
            int newQuantity = existingCart.getQuantity()+newCart.getQuantity();
            double newTotalPrice = existingCart.getTotal_price()+newCart.getTotal_price();
            String sqlUpdate = ""+
                    "UPDATE Cart SET quantity = ?, total_price = ? "+
                    "WHERE user_id = ? AND book_id = ?";
            int update = jdbcTemplate.update(
                    sqlUpdate,
                    newQuantity,
                    newTotalPrice,
                    newCart.getUser_id(),
                    newCart.getBook_id()
            );
            System.out.println("Update Result: " + update);

        }

    }
    public void deleteCart(UUID cart_id) {
        System.out.println(cart_id);
        String sql = "DELETE FROM Cart WHERE cart_id = ?";
        int update = jdbcTemplate.update(sql, cart_id);
        System.out.println("Delete Result: " + update);
    }

    public Cart getCartDetailsByCartId(UUID cart_id){
        Optional<Cart> cart = cartRepository.findById(cart_id);
        if(cart.isPresent())
            return cart.get();
        else return null;
    }
}

