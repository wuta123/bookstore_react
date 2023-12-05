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
    const [tagFetched, setTagFetched] = useState(false);
    const [searchAuthor, setSearchAuthor] = useState(false);
    const [taggedList, setTagList] = useState([]);
    const [searchTag, setSearchTag] = useState(false);
    const [titleList, setTitleList] = useState([]);
    const [searchTitle, setSearchTitle] = useState(false);
    const [titleFetched, setTitleFetched] = useState(false);
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

    const searchTagChange = () => {
        setSearchTag(!searchTag);
    }

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

    async function checkTitle(val) {
        if(!val || val == ''){
            setSearchTitle(false);
            return;
        }
        if(searchTag || searchAuthor)
            return null;
        const fetchData = await fetch('/graphql', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                query: `
                        query bookDetails($title: String) {
                            bookByTitle(title: $title) {
                                book_id,
                                title,
                                price,
                                description,
                                author,
                                type,
                                image,
                                remain,
                                sold,
                            }
                        }
                    `,
                variables: { title: val },
            }),
        });

        const data = await fetchData.json();
        if(data && data.data.bookByTitle){
            setTitleList([data.data.bookByTitle]);
        }else
            setTitleList([]);
        setTitleFetched(true);
        setSearchTitle(true);
    }

    const checkTag = async (val) => {
        if(searchTag){
            const fetchData = await fetch('/books/tag?type='+val);
            const data = await fetchData.json();
            console.log(data);
            setTagList(data);
            setTagFetched(true);
        }
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
                        <Button type={searchTag ? "default" : "primary"}
                                onClick={searchTagChange}
                                style = {{
                                    backgroundColor: searchTag ? "green":"white",
                                    color: searchTag ? "white":"green"
                                }}
                        >标签搜索</Button>
                        <Button type={searchAuthor ? "default" : "primary"}
                                onClick={searchAuthorChange}
                                style = {{
                                    backgroundColor: searchAuthor ? "green":"white",
                                    color: searchAuthor ? "white":"green"
                                }}
                        >搜索作者</Button>
                        <Search placeholder= {searchTag ? "按照标签搜索" : (searchAuthor ? "请输入书名来查找作者":"想找些什么？")}
                                onChange={(e) => setSearchValue(e.target.value)}
                                onSearch={(e) => searchTag ? checkTag(e) : (searchAuthor? checkAuthor(e) : checkTitle(e))}
                                style={{width: "300px",marginRight: "10px"}}
                        />
                    </div>

                    <div className="books-list">
                        {searchTag ? (taggedList.length > 0 ?(taggedList.map(book => (
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
                            ))):(
                                <div className="books-container">
                                    <h1 className="cartEmptyHint">没有标签"{searchValue}"对应的搜索结果</h1>
                                </div>
                            ))
                            : (searchTitle ? (
                                    titleList.length > 0 ? (titleList.map(book => (
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
                                        </div>))):(
                                        <div className="books-container">
                                            <h1 className="cartEmptyHint">没有"{searchValue}"对应的搜索结果</h1>
                                        </div>
                                    )
                                ):(bookList.length > 0 ? (bookList.map(book => (
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
                            </div>))):(
                                <div className="books-container">
                                    <h1 className="cartEmptyHint">没有"{searchValue}"对应的搜索结果</h1>
                                </div>
                                  )
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
