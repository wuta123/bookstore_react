import React, {useEffect} from 'react';
import { useState } from 'react';
import defaultAvatar from "../../picture/default-avatar.jfif"; // 默认头像
import {Button, Input} from 'antd';
import "../../css/profile.css"
import {checkAdmin} from "../../client";
import { useNavigate } from 'react-router'
import fetch from "unfetch";

const { TextArea } = Input;

const Profile = ({userInfo, setLog, }) => {
    const [name, setName] = useState(userInfo.username);
    const [managable, setManagable] = useState(false);
    const navigate= useNavigate();
    const handleManageBook = () => {//点击后经过navigate函数，切换路由到book-detail/id页面，通过state传递数据
        const navigateState = {
            state:{
                id:userInfo.id
            }
        }
        navigate(`/book_manage/${userInfo.id}`, navigateState);
    };

    useEffect(() => {
        checkAdmin(userInfo.id, setManagable).then()
    })
    const handleNameChange = (e) => {
        setName(e.target.value);
    };

    const handleManageUser = () => {
        const navigateState = {
            state:{
                id:userInfo.id
            }
        }
        navigate(`/user_manage/${userInfo.id}`, navigateState);
    }

    const handleManageOrder = () => {
        const navigateState = {
            state:{
                id:userInfo.id
            }
        }
        navigate(`/order_manage/${userInfo.id}`, navigateState);
    }

    const handleAnalytics = () => {
        const navigateState = {
            state: {
                id: userInfo.id
            }
        }
        navigate(`/consume_analytics/${userInfo.id}`, navigateState);
    }

    const handleCheckOrderAnalytics = () => {
        const navigateState = {
            state: {
                id: userInfo.id
            }
        }
        navigate(`/personal_analytics/${userInfo.id}`, navigateState);
    }

    const handleExit = () => {
        fetch(`/users/logout?username=${userInfo.username}`, {
            method: 'POST',
            credentials: 'credential',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then((data) => {
                console.log(data.msg);
                if(data.msg === 'successful'){
                    setLog(data.data);
                }
            })
        navigate(`/`)
    }

    return (
        <div className="Profile-container">
            <div className="avatar-container">
                <img src={userInfo.image} alt="Avatar" />
            </div>
            <div className="name-container">
                <p>用户名: <Input value={name} onChange={handleNameChange} /></p>
            </div>
            <div className="Btn">
                {managable ? (
                    <div className="manageBtnSet">
                        <Button className="manageBtn" onClick={handleManageBook}>
                            管理书籍
                        </Button>
                        <Button className="manageBtn" onClick={handleManageUser}>
                            管理用户
                        </Button>
                        <Button className="manageBtn" onClick={handleManageOrder}>
                            管理订单
                        </Button>
                        <Button className="manageBtn" onClick={handleAnalytics}>
                            全站数据
                        </Button>
                    </div>
                        ):(
                    <Button className="manageBtn" onClick={handleCheckOrderAnalytics}>
                        消费情况
                    </Button>
                )}
                <Button className="exitBtn" onClick={handleExit}>登出</Button>
            </div>

        </div>
    );
};
export default Profile;
