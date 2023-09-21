import React, {Component} from 'react'
import {Formik} from 'formik'
import {Input} from "antd";
import { v4 as uuidv4 } from 'uuid';
import {addBook} from "../client";
import AddNewBookToCart from "./addNewBookToCart";

class AddNewBook extends Component{

    render(){
        return(
            <div>
                <h1>请填写入库书籍信息</h1>
                <Formik
                    initialValues={{ id:this.props.id, book_id: uuidv4(), title:"",
                        description:"", author:"", type:"", image:"", remain:""
                    }}
                    validate={values => {
                        const errors = {};
                        if (!(values.price&&values.title&&values.description&&values.author&&values.type&&values.image)) {
                            errors.price = 'Required';
                            errors.title = 'Required';
                            errors.description = 'Required';
                            errors.author = 'Required';
                            errors.type = 'Required';
                            errors.image = 'Required';
                        } else if (!/^[1-9]\d*$|^0$/.test(values.price)){
                            errors.price = 'Invalid price';
                        } else if (!/^[1-9]\d*$|^0$/.test(values.remain)){
                            errors.price = 'Invalid remain';
                        }
                        return errors;
                    }}
                    onSubmit={(values, { setSubmitting }) => {
                        const bookAndId = {
                            id: values.id,
                            book: {
                                book_id:values.book_id,
                                title:values.title,
                                price:values.price,
                                description:values.description,
                                author:values.author,
                                type:values.type,
                                image:values.image,
                                remain:values.remain
                            }
                        };
                        addBook(bookAndId).then(() => {
                            alert("添加书籍成功");
                            setSubmitting(false);
                            this.props.handleChange(false);
                            this.props.refresh();
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
                                type="text"
                                name="title"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.title}
                                placeholder='书名'
                            />
                            <Input
                                type="text"
                                name="author"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.author}
                                placeholder='作者'
                            />
                            <Input
                                type="number"
                                name="price"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.price}
                                placeholder='价格'
                            />
                            <Input
                                type="text"
                                name="description"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.description}
                                placeholder='简介'
                            />
                            <Input
                                type="text"
                                name="type"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.type}
                                placeholder='类型'
                            />
                            <Input
                                type="text"
                                name="image"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.image}
                                placeholder='url格式'
                            />
                            <Input
                                type="number"
                                name="remain"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.remain}
                                placeholder='库存'
                            />
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
export default AddNewBook;
