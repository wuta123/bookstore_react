import React from 'react';
import { Link } from 'react-router-dom';
import {UserOutlined} from '@ant-design/icons'
import {Avatar} from "antd";

const Navbar = ({userIcon}) => (
  <nav>
    <div className="nav-content">
      <h1>书虫</h1>
      <div className="links">
          <h1><Link to="/books">书籍</Link></h1>
          <h1><Link to="/cart">购物车</Link></h1>
          <h1><Link to="/orders">我的订单</Link></h1>
      </div>
    </div>
      <Link to="/profile">
      {/*<UserOutlined className="icon"></UserOutlined>*/}
          <Avatar className='icon'  src={userIcon}> </Avatar>
      </Link>
  </nav>
);

export default Navbar;
