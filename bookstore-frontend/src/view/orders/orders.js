import React, {useEffect} from 'react';
import { useState } from 'react';
import {Footer} from "antd/es/layout/layout";
import { Input, Spin, Space} from 'antd';
import "../../css/orders.css"
import fetch from "unfetch";
import axios from "axios";
import {deleteOrder} from "../../client";
const { Search } = Input;


const Orders = ({userId}) => {
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
            const bookList = await Promise.all(
                data.data.map(async (item) => {
                    const book = await findBook(item.book_id);
                    return {...book, quantity: item.quantity, order_id: item.order_id}; // 将book信息加入quantity信息一起返回
                })
            );
            await (
                setOrderList(
                    {user_id: userId, bookList}
                )
            );
        };
        fetchData();
    }, []);

    const handleDelete = (order_id) => {
        deleteOrder(order_id)
            .then(() => {
                // 重新获取订单列表并更新状态
                fetch(`/orders/me?user_id=${userId}`,{
                    method: 'GET'
                })
                    .then(res => res.json())
                    .then(async data => {
                        const bookList = await Promise.all(
                            data.data.map(async (item) => {
                                const book = await findBook(item.book_id);
                                return {...book, quantity: item.quantity, order_id: item.order_id};
                            })
                        );
                        setOrderList({user_id: userId, bookList});
                    })
                    .catch(error => {
                        console.error(error);
                    });
            })
            .catch(error => {
                console.error(error);
                // 报错
            });
    }


    const filteredList = orderList.bookList ? (orderList.bookList.filter(book =>
        book.title && book.title.includes(searchValue))) : [];



    return (
        <div className="Order">
            {orderList.bookList ? ( // 判断是否加载完成
                <div>
                    <div className="order-search-bar">
                        <Search
                            placeholder="想找些什么？"
                            onChange={(e) => setSearchValue(e.target.value)}
                            style={{ width: '300px', marginRight: '10px' }}
                        />
                    </div>
                    {orderList.bookList.length > 0 ? (
                    <div className="bookList2">
                        {filteredList.map((book) => (
                            <div className="Order-container">
                                <div className="book">
                                    <img
                                        src={book.image}
                                        alt={`${book.title} book cover`}
                                    />
                                    <div className="book-details">
                                        <h3>{book.title}</h3>
                                        <p>作者： {book.author}</p>
                                        <p>数量： {book.quantity}</p>
                                        <p>价格： ¥{book.quantity * book.price}</p>
                                        {/*计算总价*/}
                                    </div>
                                    <button onClick={() => handleDelete(String(book.order_id))}>
                                        移除
                                    </button>
                                </div>
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
