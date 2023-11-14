import React, {useEffect, useState} from 'react';
import Book from '../../components/Book';
import '../../css/books.css'; // 引入CSS
import {Button, Input, Space, Spin} from 'antd';
import fetch from 'unfetch'
import {useNavigate} from "react-router";
const { Search } = Input;



const Books = function({userId}) {
    const [bookList, setBookList] = useState([]);
    const [fetched, setFetched] = useState(false);
    const [searchAuthor, setSearchAuthor] = useState(false);
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

    const searchAuthorChange = () => {
        setSearchAuthor(!searchAuthor);
    }

    const filteredList = bookList.filter(book =>
        book.title.includes(searchValue)
    );

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

    const checkAuthor = async (val) => {
        if(searchAuthor){
            const fetchData = await fetch('http://localhost:8060/api/book/author/'+val);
            const data = await fetchData.json();
            showMessage("《"+val + "》的作者是："+data.author, 3000);
        }
        return null;
    }

    return (
            <div className="books">
                <div id="messageBox" className="message-box">
                    <span id="messageText" className="message-text"></span>
                </div>
            {(bookList.length || fetched) ? (
                <div>
                    <h1 align="center">Books </h1>


                    <div className="search-bar">
                        <Button type={searchAuthor ? "default" : "primary"}
                                onClick={searchAuthorChange}
                                style = {{
                                    backgroundColor: searchAuthor ? "green":"white",
                                    color: searchAuthor ? "white":"green"
                                }}
                        >搜索作者</Button>
                        <Search placeholder= {searchAuthor ? "请输入书名来查找作者":"想找些什么？"}
                                onChange={(e) => setSearchValue(e.target.value)}
                                onSearch={(e) => checkAuthor(e)}
                                style={{width: "300px",marginRight: "10px"}}
                        />
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

export default Books;
