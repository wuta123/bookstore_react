import React, {Component} from 'react'
import {Formik} from 'formik'
import {Input} from "antd";
import {addNewItemToCart} from "../client";

class AddNewBookToCart extends Component{

    render(){
        return(
            <div>
                <h1>请填写需要加入购物车的数量</h1>
                <Formik
                    initialValues={{ user_id:this.props.user_id, book_id:this.props.book_id, quantity: 1}}
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
                            addNewItemToCart(values).then(() => {
                                alert("添加购物车成功");
                                setSubmitting(false);
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
                                placeholder='1'
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

export default AddNewBookToCart;
