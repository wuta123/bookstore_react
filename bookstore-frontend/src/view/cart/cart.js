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
        await purchaseAllCartItem(purchaseList)
        showMessage("购买成功", 3000)
        refreshCart();
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

        showMessage("已经清空购物车！", 3000);
        await purchaseAllCartItem(purchaseList);
        refreshCart();
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
