import React, {Component} from 'react'
import {Formik} from 'formik'
import {Input} from "antd";
import {purchaseItem} from "../client";

class PurchaseAListOfBooks extends Component{
    render(){
        return(
            <div>
                <h1>确认购买的数量</h1>
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
                            values.total_price = values.quantity*this.props.price;
                        }
                        return errors;
                    }}
                    onSubmit={(values, { setSubmitting }) => {
                            purchaseItem(values).then(async (res) => {
                                const data = await res.json();
                                if(data.msg === 'success') {
                                    setSubmitting(false);
                                    alert("订单信息已经确认");
                                }
                                else{
                                    alert("您购买的部分商品库存不足，请检查");
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
