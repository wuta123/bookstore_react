import React, {useEffect, useState} from "react";
import "../../css/login.css";
import fetch from "unfetch";
import { EyeInvisibleOutlined, EyeTwoTone } from '@ant-design/icons';
import { Button, Input, Space } from 'antd';
import {useNavigate} from "react-router";
const Login = ({logged}) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordVisible, setPasswordVisible] = React.useState(false);
    const navigate = useNavigate();
    const handleLogin = () => {
        console.log("inputUserName: ", username, ",password: ", password);
        fetch(`/users/login?username=`+username+"&password="+password, {
            method: 'POST',
            credentials: 'credential',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then((data) => {
                console.log(data.msg);
                if(data.msg === 'success'){
                    logged(true, data.data);
                }else if(data.msg === 'ban'){
                    alert("您的账号已被封禁！");
                }else{
                    alert("您输入的用户不存在或密码有误");
                }
            })
    };


    const handleRegister=()=>{
        navigate(`/register`);
    }

    return (

        <div className="login-container">
            <Space direction="vertical">
                <h1 align="center">Login</h1>
                <div className="form-container">
                    <Space>
                        <label>用户名:</label>
                        <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                    </Space>
                    <Space direction="horizontal">
                    <label>密码:</label>
                    {/*<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />*/}
                    <Input.Password
                        value = {password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="input password"
                        iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                    />
                    </Space>
                    <Space direction="horizontal">
                    <button onClick={handleLogin}>Login</button>
                    <button onClick={handleRegister}>Register</button>
                    </Space>
                </div>
            </Space>
        </div>
    );
};

export default Login;

// import React from 'react';
// import { EyeInvisibleOutlined, EyeTwoTone } from '@ant-design/icons';
// import { Button, Input, Space } from 'antd';
//
// const App: React.FC = () => {
//     const [passwordVisible, setPasswordVisible] = React.useState(false);
//
//     return (
//         <Space direction="vertical">
//             <Input.Password placeholder="input password" />
//             <Input.Password
//                 placeholder="input password"
//                 iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
//             />
//             <Space direction="horizontal">
//                 <Input.Password
//                     placeholder="input password"
//                     visibilityToggle={{ visible: passwordVisible, onVisibleChange: setPasswordVisible }}
//                 />
//                 <Button style={{ width: 80 }} onClick={() => setPasswordVisible((prevState) => !prevState)}>
//                     {passwordVisible ? 'Hide' : 'Show'}
//                 </Button>
//             </Space>
//         </Space>
//     );
// };
//
// export default App;
