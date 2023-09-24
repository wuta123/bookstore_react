import {BrowserRouter, Routes, Route} from 'react-router-dom';
import {useState} from "react";
import Books from './view/books/books';
import Navbar from './components/Navbar';
import Cart from './view/cart/cart';
import Profile from './view/profile/profile';
import BookDetail from "./view/books/bookDetail";
import Login from "./view/login/login"
import './css/App.css';
import Orders from "./view/orders/orders";
import BookManage from "./view/books/bookManage";
import {checkAdmin} from "./client";
import UserManage from "./view/profile/userManage";
import OrderManage from "./view/orders/orderManage";
import Register from "./view/login/register";
import ConsumeAnalytics from "./view/orders/consumeAnalytics";
import ConsumePersonal from "./view/orders/consumePersonal";

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userInfo, setUserInfo] = useState({});
    const [admin, setAdmin] = useState(false);
    const handleLogin = async (isLoggedIn, userInfo) => {
        setIsLoggedIn(isLoggedIn);
        setUserInfo(userInfo);
        setDefaultElement(isLoggedIn ? <Books/> : <Login logged={handleLogin}/>);
        if (isLoggedIn) await checkAdmin(userInfo.user_id, setAdmin).then();
        if (isLoggedIn){
            showMessage("成功登录！登录时间为："+ Date(), 3000);
        }
    };

    const handleLogout = (msg) => {
        setIsLoggedIn(false);
        setUserInfo({});
        setDefaultElement(<Login logged={handleLogin}/>);
        setAdmin(false);
        showMessage(msg, 3000);
    }

    function showMessage(message, duration) {
        let messageBox = document.getElementById("messageBox");
        let messageText = document.getElementById("messageText");

        // 设置消息文本
        messageText.innerText = message;

        // 显示消息框
        messageBox.style.display = "block";

        // 设置定时器，在指定时间后关闭消息框
        setTimeout(function() {
            messageBox.style.display = "none";
        }, duration);
    }

    const [defaultElement, setDefaultElement] = useState(<Login logged={handleLogin}/>);
    return (
        <BrowserRouter>
            {isLoggedIn && <Navbar userIcon={userInfo.image}/>}
            <div className="main">
                <div id="messageBox" className="message-box">
                    <span id="messageText" className="message-text"></span>
                </div>
                <Routes>
                    <Route path="/" element={defaultElement} />
                    <Route path="/register" element={<Register/>}/>
                    <Route path="/personal_analytics/:user_id" element={<ConsumePersonal user_id={userInfo.id}/>}/>
                    {(isLoggedIn && <Route path="/books" element={<Books userId={userInfo.id}/>} />) || <Route path="/books" element={<Login logged={handleLogin}/>} />}
                    {(isLoggedIn && <Route path="/cart" element={<Cart userId={userInfo.id}/>} />) || <Route path="/cart" element={<Login logged={handleLogin}/>} />}
                    {(isLoggedIn && <Route path="/profile" element={<Profile userInfo={userInfo} setLog={handleLogout}/>} />) || <Route path="/profile" element={<Login logged={handleLogin}/>} />}
                    {(isLoggedIn && <Route path="/orders" element={<Orders userId={userInfo.id}/>} />) || <Route path="/orders" element={<Login logged={handleLogin}/>} />}
                    {(isLoggedIn && <Route path="/book-detail/:id" element={<BookDetail userId={userInfo.id}/>} />) || <Route path="/book-detail/:id" element={<Login logged={handleLogin}/>}/>}
                    {(isLoggedIn && admin && <Route path="/book_manage/:userid" element={<BookManage userId={userInfo.id}/>}/>) || <Route path="/book_manage/:userid" element={<Login logged={handleLogin}/>}/>}
                    {(isLoggedIn && admin && <Route path="/user_manage/:user_id" element={<UserManage user_id={userInfo.id}/>}/>)|| <Route path="/user_manage/:userid" element={<Login logged={handleLogin}/>}/>}
                    {(isLoggedIn && admin && <Route path="/order_manage/:user_id" element={<OrderManage user_id={userInfo.id}/>}/>)||<Route path="/order_manage/:userid" element={<Login logged={handleLogin}/>}/>} }
                    {(isLoggedIn && admin && <Route path="/consume_analytics/:user_id" element={<ConsumeAnalytics user_id={userInfo.id}/>}/>) || <Route path="/consume_analytics/:userid" element={<Login logged={handleLogin}/>}/>}}
            </Routes>
            </div>
        </BrowserRouter>
    );
}

export default App;
