import React, { useEffect, useState } from 'react';
import { Formik } from 'formik';
import { Input } from "antd";
import { v4 as uuidv4 } from 'uuid';
import { addBook, getBookById, editBook } from "../client";
import AddNewBookToCart from "./addNewBookToCart";

const SetABook = ({ id, book, handleChange, refresh}) => {
    return (
        <div>
            <h1>编辑书籍信息</h1>
            <Formik
                initialValues={{
                    id: id,
                    title: book.title || "",
                    description: book.description || "",
                    author: book.author || "",
                    price: book.price,
                    type: book.type || "",
                    image: book.image || "",
                    remain: book.remain || 0
                }}
                validate={values => {
                    const errors = {};
                    if (!(values.price && values.title && values.description && values.author && values.type && values.image)) {
                        errors.price = 'Required';
                        errors.title = 'Required';
                        errors.description = 'Required';
                        errors.author = 'Required';
                        errors.type = 'Required';
                        errors.image = 'Required';
                        errors.remain = 'Required';
                    } else if (!/^[1-9]\d*$|^0$/.test(values.price)) {
                        errors.price = 'Invalid price';
                    } else if (!/^[1-9]\d*$|^0$/.test(values.remain)) {
                        errors.price = 'Invalid remain';
                    }
                    return errors;
                }}
                onSubmit={(values, { setSubmitting }) => {
                    const bookAndId = {
                        id: values.id,
                        book: {
                            book_id: book.book_id,
                            title: values.title,
                            price: values.price,
                            description: values.description,
                            author: values.author,
                            type: values.type,
                            image: values.image,
                            remain: values.remain
                        }
                    };
                    editBook(bookAndId)
                        .then(async (res) => {
                            const data = await res.json();
                            if (data.msg === 'success') {
                                alert("编辑书籍成功");
                                setSubmitting(false);
                                handleChange(false);
                                refresh();
                            } else {
                                alert("编辑书籍失败");
                                setSubmitting(false);
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
                        <text>
                            书名
                        </text>
                        <Input
                            type="text"
                            name="title"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.title}
                            placeholder='书名'
                        />
                        <text>
                            作者
                        </text>
                        <Input
                            type="text"
                            name="author"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.author}
                            placeholder='作者'
                        />
                        <text>
                            价格
                        </text>
                        <Input
                            type="number"
                            name="price"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.price}
                            placeholder='价格'
                        />
                        <text>
                            简介
                        </text>
                        <Input
                            type="text"
                            name="description"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.description}
                            placeholder='简介'
                        />
                        <text>
                            类型
                        </text>
                        <Input
                            type="text"
                            name="type"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.type}
                            placeholder='类型'
                        />
                        <text>
                            图片
                        </text>
                        <Input
                            type="text"
                            name="image"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.image}
                            placeholder='url格式'
                        />
                        <text>
                            库存
                        </text>
                        <Input
                            type="number"
                            name="remain"
                            onChange={handleChange}
                            onBlur={handleBlur}
                            value={values.remain}
                            placeholder='库存'
                        />
                        <button onClick={() => submitForm()} type="submit" disabled={isSubmitting}>
                            确认
                        </button>
                    </form>
                )}
            </Formik>
        </div>
    );
};

export default SetABook;
