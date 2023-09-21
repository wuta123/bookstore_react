import React, {useEffect, useState} from 'react';
import Book from '../../components/Book';
import '../../css/books.css'; // 引入CSS
import {Button, Input, Modal, Space, Spin} from 'antd';
import fetch from 'unfetch'
import {deleteBook} from "../../client";
import AddNewBook from "../../utils/addNewBook";
import SetABook from "../../utils/setABook";
const { Search } = Input;


const BookManage = function({userId}) {

    const [bookList, setBookList] = useState([]);
    const [addNewBook, setAddNewBook] = useState(false);
    const [editABook, setEditABook] = useState(false);
    const [theBook, setTheBook] = useState({});
    const [fetched, setFetched] = useState(false);


    useEffect(() => {
        const fetchData = async () => {
            const result = await fetch('/books');
            const data = await result.json();
            setBookList(data);
            setFetched(true);
        };
        fetchData();
    }, []);

    const [searchValue, setSearchValue] = useState("");

    const refresh = ()=>{
        const fetchData = async () => {
            const result = await fetch('/books');
            const data = await result.json();
            setBookList(data);
        };
        fetchData();
    }


    const filteredList = bookList.filter(book =>
        book.title.includes(searchValue)
    );

    return (
        <div className="books">
            <Modal title="添加新书籍"
                   open={addNewBook}
                   onOk={()=>setAddNewBook(false)}
                   onCancel={()=>setAddNewBook(false)}
            >
                <AddNewBook id={userId} handleChange={setAddNewBook} refresh={refresh}/>
            </Modal>

            {editABook?(
                <Modal title="编辑书籍"
                       open={true}
                       onOk={()=>{
                           setEditABook(false)
                           setTheBook({});
                       }
                       }
                       onCancel={()=>{
                           setEditABook(false)
                           setTheBook({});
                       }
                       }
                >
                    <SetABook id={userId} book = {theBook} handleChange={setEditABook} refresh={refresh}/>
                </Modal>
            ):(<></>)}


            {(bookList.length || fetched) ? (
                <div>
                    <h1 align="center">BookManage</h1>
                    <div className="search-bar">
                        <Search placeholder="想找些什么？"
                                onChange={(e) => setSearchValue(e.target.value)}
                                style={{width: "300px",marginRight: "10px"}}
                        />
                        <Button onClick= {()=>setAddNewBook(true)}>+</Button>
                    </div>

                    <div className="books-list">
                        {filteredList.map(book => (
                            <div className="books-container">
                                <div>
                                    <Book
                                        book_id={book.book_id}
                                        title={book.title}
                                        price={book.price}
                                        author={book.author}
                                        description={book.description}
                                        type={book.type}
                                        image={book.image}
                                        remain={book.remain}
                                        sold={book.sold}
                                    />
                                    <Button onClick={()=>{
                                        deleteBook(userId, book.book_id).then(alert("删除成功！"))
                                        refresh()
                                    }}>删除</Button>
                                    <Button onClick={()=>{
                                        setTheBook(book);
                                        setEditABook(true);
                                    }}>编辑</Button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            ):(
                <Space className="hintHolder2" direction="vertical" align="center">
                    <Spin
                        className="spinIcon2"
                        size="large"
                    />
                    <h4 className="hintTitle2">
                        书籍加载中
                    </h4>
                </Space>
            )}
        </div>
    )
}

export default BookManage;
