import React, {Component} from 'react'
import {Formik} from 'formik'
import {Input} from "antd";
import {purchaseItem} from "../client";
import {closeWebSocket, createWebSocket} from "./websocketServer";

class PurchaseAListOfBooks extends Component{

    render(){
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

        let order_id;

        function handleEvent(event){
            console.log(event.data.toString().substring(1, event.data.toString().length-1));
            console.log(order_id);
            if(event.data.toString().substring(1, event.data.toString().length-1) === order_id
                || event.data.toString() === order_id
            )
            showMessage2("本次订单已经成功结束", 3000);
            closeWebSocket();
        }


        return(
            <div>
                <h1>确认购买的数量</h1>
                <div id="messageBox" className="message-box">
                    <span id="messageText" className="message-text"></span>
                </div>
                <div id="messageBox2" className="message-box2">
                    <span id="messageText2" className="message-text"></span>
                </div>
                <Formik
                    initialValues={{ user_id:this.props.user_id, book_id:this.props.book_id, quantity: 1, total_price: this.props.price}}
                    validate={values => {
                        const errors = {};
                        if (!values.quantity) {
                            errors.quantity = 'Required';
                        } else if (!/^[1-9]\d*$|^0$/.test(values.quantity)){
                            errors.quantity = 'Invalid quantity';
                        }else if(values.quantity > 100){
                            errors.quantity = 'Quantity should be between 1 and 100';
                        }else{
                            values.total_price = values.quantity * this.props.price;
                        }
                        return errors;
                    }}
                    onSubmit={(values, { setSubmitting }) => {
                            createWebSocket("ws://localhost:8080/websocket/transfer/"+this.props.user_id, handleEvent)
                            purchaseItem(values).then(async (res) => {
                                const data = await res.json();
                                if(data.msg === 'success') {
                                    order_id = data.data.toString();
                                    setSubmitting(false);
                                    showMessage("订单信息已经确认，单号为："+order_id, 3000);
                                }
                            })
                    }}
                >
                    {({
                          values,
                          errors,
                          touched,
                          handleChange,
                          handleBlur,
                          handleSubmit,
                          isSubmitting,
                          submitForm,
                          /* and other goodies */
                      }) => (
                        <form onSubmit={handleSubmit}>
                            <Input
                                type="quantity"
                                name="quantity"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.quantity}
                                placeholder={values.quantity}
                            />
                            {errors.quantity && touched.quantity && errors.quantity}
                            <button onClick = {()=>submitForm()} type="submit" disabled={isSubmitting}>
                                确认
                            </button>
                        </form>
                    )}
                </Formik>
            </div>
        );
    }
}

export default PurchaseAListOfBooks;
