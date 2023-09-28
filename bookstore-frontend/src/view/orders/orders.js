import React, {useEffect} from 'react';
import { useState } from 'react';
import {Footer} from "antd/es/layout/layout";
import { Input, Spin, Space} from 'antd';
import "../../css/orders.css"
import fetch from "unfetch";
import axios from "axios";
import {deleteOrder} from "../../client";
const { Search } = Input;


const Orders = ({userId, showMessage}) => {
    const [searchValue, setSearchValue] = useState("");

    const [orderList, setOrderList] = useState({});

    const findBook = async(bookId) => {
        const result = await fetch('/books');
        const data = await result.json();
        const book = data.find((book) => book.book_id === bookId);
        return book;
    }


    useEffect(() => {
        const fetchData = async () => {
            const result = await fetch(`/orders/me?user_id=${userId}`,{
                method: 'GET'
            });
            const data = await result.json();//data保存了所有的购物车列表
            data.orders = await Promise.all(
                data.orders.map(async (item) => {
                    item.orderitems = await Promise.all(
                            item.orderitems.map(async (oneItem) => {
                                const book = await findBook(oneItem.book_id);
                                return {...book, quantity: oneItem.quantity, order_id: oneItem.order_id, total_price: oneItem.total_price};
                            })
                    )
                    return item;
                })
            );
            console.log(data.orders);
            await (
                setOrderList(
                    {user_id: userId, orders: data.orders}
                )
            );
        };
        fetchData();
    }, []);

    function refreshOrder() {
            // 重新获取订单列表并更新状态
        fetch(`/orders/me?user_id=${userId}`,{
            method: 'GET'
        }).then(res => res.json()).then(
            async data => {
                data.orders = await Promise.all(
                    data.orders.map(async (item) => {
                        item.orderitems = await Promise.all(
                            item.orderitems.map(async (oneItem) => {
                                const book = await findBook(oneItem.book_id);
                                return {...book, quantity: oneItem.quantity, order_id: oneItem.order_id, total_price: oneItem.total_price};
                            })
                        )
                        return item;
                    })
                );
                console.log(data.orders);
                setOrderList(
                    {user_id: userId, orders: data.orders}
                )
            }
        )
    }

    const handleDelete = async (order_id) => {
        await deleteOrder(order_id)
        showMessage("成功删除", 3000)
        refreshOrder();
    }


    const filteredList = orderList.orders ? (
        orderList.orders.filter(order =>
        {
            let filteredItemList = order.orderitems.filter(book => book.title && book.title.includes(searchValue));
            return filteredItemList.length > 0;
        }).reverse()
    ) : [];



    return (
        <div className="Order">
            {orderList.orders ? ( // 判断是否加载完成
                <div>
                    <div className="order-search-bar">
                        <Search
                            placeholder="想找些什么？"
                            onChange={(e) => setSearchValue(e.target.value)}
                            style={{ width: '300px', marginRight: '10px' }}
                        />
                    </div>
                    {orderList.orders.length > 0 ? (
                    <div className="bookList2">
                        {filteredList.map((order) => (
                            <div className="Order-container">
                                <Space>订单编号{order.order_id}</Space>
                                {order.orderitems.map((book) => (
                                <div className="book">
                                    <img
                                        src={book.image}
                                        alt={`${book.title} book cover`}
                                    />
                                    <div className="book-details">
                                        <h3>{book.title}</h3>
                                        <p>作者： {book.author}</p>
                                        <p>数量： {book.quantity}</p>
                                        <p>价格： ¥{book.total_price}</p>
                                        {/*计算总价*/}
                                    </div>
                                    <button onClick={() => handleDelete(String(order.order_id))}>
                                        移除
                                    </button>
                                </div>
                                ))
                                }
                                <Space>下单时间{order.purchase_time}</Space>
                            </div>
                        ))}
                    </div>
                    ):(
                        <h1 className="emptyOrder" align="center">您的订单空空如也</h1>
                    )}
                </div>
            ) : (
                <Space className="hintHolder" direction="vertical" align="center">
                    <Spin
                        className="spinIcon"
                        size="large"
                    ></Spin>
                    <h4 className="hintTitle">
                        订单信息加载中
                    </h4>
                </Space>
            )}
        </div>
    );
};
export default Orders;
