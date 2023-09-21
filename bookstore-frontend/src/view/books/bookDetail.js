import { Descriptions } from 'antd';
import { Button, Modal } from 'antd'
import React, {useState} from 'react';
import { ShoppingCartOutlined, PayCircleOutlined } from '@ant-design/icons'
import { useLocation } from 'react-router-dom';
import AddNewBookToCart from '../../utils/addNewBookToCart'
import PurchaseAListOfBooks from "../../utils/purchaseAListOfBooks"
import "../../css/bookDetail.css";



function BookDetail({userId}){//react页面，接受路由申请
    const {state:{id, title, price, author, description, type, image, remain, sold} = {}} = useLocation();
    const [openAddCartModal, setCartState] = useState(false);
    const [openPurchaseModal, setPurchaseState] = useState(false);
    function handleClick() {
        setCartState(true);
    }

    function handleClick2(){
        setPurchaseState(true);
    }
    function handleClose(){
        setCartState(false);
    }

    function handleClose2(){
        setPurchaseState(false);
    }

    return (

        <div className="content">
            <h1 className="bkdetail">图书详情</h1>
            <div className="book-detail">
                <div className="book-image">
                    <img alt="image" src={image} style={{width:"350px", height:"350px"}}/>
                </div>

                <div className="descriptions">
                    <Descriptions>
                        <Descriptions.Item label="书名" className="title">{title}</Descriptions.Item>
                        <Descriptions.Item label="作者" className="author">{author}</Descriptions.Item>
                        <Descriptions.Item label="类型" className="type">{type}</Descriptions.Item>
                        <Descriptions.Item label="价格" className="price">{price}</Descriptions.Item>
                        <Descriptions.Item label="详情" className="descriptions">{description}</Descriptions.Item>
                    </Descriptions>
                    <Descriptions>
                        <Descriptions.Item label="库存" className="remain">{remain}</Descriptions.Item>
                        <Descriptions.Item label="销量" className="sold">{sold}</Descriptions.Item>
                    </Descriptions>
                </div>
            </div>


            <div className="button-groups">
                <Button type="danger" icon={<ShoppingCartOutlined />} size={"large"} onClick={handleClick}>
                    加入购物车
                </Button>
                <Modal title="添加进购物车"
                       open={openAddCartModal}
                       onOk={handleClose}
                       onCancel={handleClose}
                >
                    <AddNewBookToCart book_id={id} user_id={userId} price={price}/>
                </Modal>
                <Button type="danger" icon={<PayCircleOutlined />} size={"large"} style={{marginLeft:"15%"}} onClick={handleClick2}>
                    立即购买
                </Button>
                <Modal title="立即购买"
                       open={openPurchaseModal}
                       onOk={handleClose2}
                       onCancel={handleClose2}
                >
                    <PurchaseAListOfBooks book_id={id} user_id={userId} quantity={1} price={price} />
                </Modal>
            </div>
        </div>
    )
}

export default BookDetail;
