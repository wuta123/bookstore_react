import React, {useEffect, useState} from "react";
import fetch from "unfetch";
import '../../css/register.css'
import { EyeInvisibleOutlined, EyeTwoTone } from '@ant-design/icons';
import { Button, Input, Space } from 'antd';
import {useNavigate} from "react-router";

const Register = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordVisible, setPasswordVisible] = React.useState(false);
    const [email, setEmail] = useState("");
    const [image, setImage] = useState("");
    const navigate = useNavigate();
    const goBack = () => {
        navigate(`/`);
    }
    const handleRegister = () => {
        // 对用户输入的信息进行检查
        // 1. 用户名必须只能包括英文字母、阿拉伯数字以及下划线，不得包括其它的非法字符，否则 alert("用户名只能包含英文字母、阿拉伯数字和下划线!")
        if (!/^[a-zA-Z0-9_]+$/.test(username) || username==='') {
            alert("用户名不得为空，且只能包含英文字母、阿拉伯数字和下划线!");
            return;
        }

        // 2. 邮箱必须是 xxx@xxx.xxx 的格式，其中 xxx 只能由字母和数字组成，否则 alert("邮箱格式不合法！")
        if (!/^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z0-9]+$/.test(email)) {
            alert("邮箱格式不合法！");
            return;
        }

        // 3. 密码的长度必须在 4~16 位之间，可以为任意字符，否则 alert("密码长度必须在 4~16 位之间!")
        if (password.length < 4 || password.length > 16) {
            alert("密码长度必须在 4~16 位之间!");
            return;
        }

        // 4. confirmPassword 和 password 的值需要一致，否则 alert("两次输入密码值不一致！")
        if (confirmPassword !== password) {
            alert("两次输入密码值不一致！");
            return;
        }

        if(image===""){
            setImage("https://img.zcool.cn/community/01a3865ab91314a8012062e3c38ff6.png@1280w_1l_2o_100sh.png");
        }

        // 发送注册请求
        fetch(`/users/register?username=${username}&password=${password}&email=${email}&image=${image}`, {
            method: 'GET',
        })
            .then(response => response.json())
            .then((data) => {
                console.log(data.msg);
                if (data.msg === 'success') {
                    alert("已经注册！请返回原页面登录");
                } else {
                    alert("您的用户名或邮箱已被占用");
                }
            })
    };


    return (

        <div className="register-container">
            <Space direction="vertical">
                <h1 align="center">Login</h1>
                <div className="form-container">
                    <Space className="space" direction="horizontal">
                        <label>用户名:</label>
                        <Input type="text" style={{backgroundColor: 'white', borderWidth:1, borderColor: 'black'}} value={username} onChange={(e) => setUsername(e.target.value)} />
                    </Space>
                    <Space className="space" direction="horizontal">
                        <label>邮箱:</label>
                        <Input type="text" style={{backgroundColor: 'white', borderWidth:1, borderColor: 'black'}} value={email} onChange={(e) => setEmail(e.target.value)} />
                    </Space>
                    <Space className="space" direction="horizontal">
                        <label>头像url:</label>
                        <Input type="text" style={{backgroundColor: 'white', borderWidth:1, borderColor: 'black'}} value={image} onChange={(e) => setImage(e.target.value)} />
                    </Space>
                    <Space className="space" direction="horizontal">
                        <label>密码:</label>
                        {/*<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />*/}
                        <Input.Password
                            value = {password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="input password"
                            iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                        />
                    </Space>
                    <Space className="space" direction="horizontal">
                        <label>确认密码:</label>
                        {/*<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />*/}
                        <Input.Password
                            value = {confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="confirm password"
                            iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                        />
                    </Space>
                    <Space className="space" direction="horizontal">
                        <button onClick={handleRegister}>Register</button>
                        <button onClick={goBack}>Exit</button>
                    </Space>
                </div>
            </Space>
        </div>
    );
};

export default Register;
