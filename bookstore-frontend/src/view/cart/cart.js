import React, {useEffect} from 'react';
import { useState } from 'react';
import testImage from "../../picture/testexamples.png";
import {Input, Spin, Space, Modal} from 'antd';
import "../../css/cart.css"
import fetch from "unfetch";
import {Footer} from "antd/es/layout/layout";
import PurchaseAListOfBooks from "../../utils/purchaseAListOfBooks";
import axios from "axios";
import {deleteCart, deleteOrder, purchaseAllCartItem, purchaseItem} from "../../client";
import Book from "../../components/Book";
import {closeWebSocket, createWebSocket} from "../../utils/websocketServer";
import orders from "../orders/orders";
const { Search } = Input;


const Cart = ({userId, showMessage}) => {
    const [searchValue, setSearchValue] = useState("");
    const [cartList, setCartList] = useState({});
    const findBook = async(book_id) => {
        const result = await fetch('/books');
        const data = await result.json();
        const book = data.find((book) => book.book_id === book_id);
        return book;
    }

    let order_id = null;

    function showMessage2(message, duration) {
        let messageBox = document.getElementById("messageBox2");
        let messageText = document.getElementById("messageText2");

        // 设置消息文本
        messageText.innerText = message;

        // 显示消息框
        messageBox.style.display = "block";

        // 设置定时器，在指定时间后关闭消息框
        setTimeout(function() {
            messageBox.style.display = "none";
        }, duration);
    }

    function handleEvent(event){
        console.log(event.data.toString().substring(1, event.data.toString().length-1));
        console.log(order_id);
        if(event.data.toString().substring(1, event.data.toString().length-1) === order_id
            || event.data.toString() === order_id
        )
            showMessage2("本次订单已经成功结束", 3000);
        closeWebSocket();
        refreshCart();
    }

    function refreshCart(){
        // 重新获取购物车列表并更新状态
        fetch('/carts')
            .then(res => res.json())
            .then(async data => {
                const cart = data.filter((theCart) => (theCart.user_id == userId));
                const bookList = await Promise.all(
                    cart.map(async (item) => {
                        const book = await findBook(item.book_id);
                        return {...book, quantity: item.quantity, cart_id: item.cart_id};
                    })
                );
                setCartList({user_id: userId, bookList});
            })
            .catch(error => {
                console.error(error);
            });
    }

    async function handlePurchase(user_id, book_id, quantity, total_price, cart_id) {
        let purchaseList = [
            {
                book_id : book_id,
                user_id : user_id,
                cart_id : cart_id
            }
        ]
        purchaseAllCartItem(purchaseList).then(async res => {
            const data = await res.json();
            order_id = data.data.toString();
        })
        createWebSocket("ws://localhost:8080/websocket/transfer/"+userId, handleEvent)
        showMessage("购买成功", 3000)
    }


    async function handlePurchaseAll() {
        console.log(cartList.bookList);
        let purchaseList = cartList.bookList.map(book => {
            return {
                book_id : book.book_id,
                user_id: cartList.user_id,
                cart_id: book.cart_id
            }
        })
        purchaseAllCartItem(purchaseList).then(async res => {
            const data = await res.json();
            order_id = data.data.toString();
        });
        createWebSocket("ws://localhost:8080/websocket/transfer/"+userId, handleEvent)
        showMessage("已经清空购物车！", 3000);
        // 重新获取购物车列表并更新状态
    }



    useEffect(() => {
        const fetchData = async () => {
            const result = await fetch('/carts');
            const data = await result.json();//data保存了所有的购物车列表
            const cart = data.filter((theCart) => (theCart.user_id == userId)); // 取出购物车列表当中id和用户id相匹配的那一个
            const bookList = await Promise.all(
                cart.map(async (item) => {
                    const book = await findBook(item.book_id);
                    return {...book, quantity: item.quantity, cart_id: item.cart_id}; // 将book信息加入quantity信息一起返回
                })
            );
            await (
                setCartList(
                {user_id: userId, bookList}
                )
            ); // 构造并更新cartList
        };
        fetchData();
    }, []);

    const handleDelete = (cart_id) => {
        deleteCart(cart_id)
            .then(refreshCart)
            .catch(error => {
                console.error(error);
                // 报错
            });
    }

    const filteredList = cartList.bookList ? (cartList.bookList.filter(book =>
        book.title && book.title.includes(searchValue))) : [];

    return (
        <div className="Cart">
            <div id="messageBox2" className="message-box2">
                <span id="messageText2" className="message-text"></span>
            </div>
            {cartList.bookList ? ( // 判断是否加载完成
                <div>
                    <div className="cart-search-bar">
                        <Search
                            placeholder="想找些什么？"
                            onChange={(e) => setSearchValue(e.target.value)}
                            style={{ width: '300px', marginRight: '10px' }}
                        />
                    </div>
                    {cartList.bookList.length > 0 ?(
                    <div className="bookList2">
                            {filteredList.map((book) => (
                                <div className="Cart-container">
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
                                        <button onClick={() => handleDelete(book.cart_id)}>
                                            移除
                                        </button>
                                        <button onClick={async () => {
                                            await(handlePurchase(userId, book.book_id, book.quantity, book.quantity*book.price, book.cart_id))
                                        }}>
                                            购买
                                        </button>
                                    </div>
                                </div>
                            ))}
                        <Footer className='orderFooter'>
                            <button className='purchaseAllBtn'
                                    onClick={() => handlePurchaseAll()}>
                                全部购买
                            </button>
                        </Footer>
                    </div>) :(
                        <h1 className="cartEmptyHint" align="center">您的购物车空空如也</h1>
                    )}
                </div>
            ) : (
                <Space className="hintHolder" direction="vertical" align="center">
                    <Spin
                        className="spinIcon"
                        size="large"
                        ></Spin>
                    <h4 className="hintTitle">
                        购物车信息加载中
                    </h4>
                </Space>
            )}
        </div>
    );
};
export default Cart;
