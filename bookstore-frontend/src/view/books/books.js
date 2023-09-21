import React, {useEffect, useState} from 'react';
import Book from '../../components/Book';
import '../../css/books.css'; // 引入CSS
import {Input, Space, Spin} from 'antd';
import fetch from 'unfetch'
import {useNavigate} from "react-router";
const { Search } = Input;



const Books = function({userId}) {
    const [bookList, setBookList] = useState([]);
    const [fetched, setFetched] = useState(false)
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

    const filteredList = bookList.filter(book =>
        book.title.includes(searchValue)
    );

    return (
            <div className="books">
            {(bookList.length || fetched) ? (
                <div>
                    <h1 align="center">Books </h1>
                    <div className="search-bar">
                        <Search placeholder="想找些什么？"
                                onChange={(e) => setSearchValue(e.target.value)}
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
