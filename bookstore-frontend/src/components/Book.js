import React from 'react'
import '../css/book.css';
import { useNavigate } from 'react-router'


function Book(props) {

    const { book_id, title, price, author, description, type, image, remain, sold} = props;
    const navigate = useNavigate();
    const handleClickBook = () => {//点击后经过navigate函数，切换路由到book-detail/id页面，通过state传递数据
        const navigateState = {
            state:{
                id: book_id,
            }
        }
        navigate(`/book-detail/${book_id}`, navigateState);
    };

    return (
        <div className="book-container" onClick={handleClickBook}>
            <div className="book-info">
                <h2 className="title">{title}</h2>
                <p className="author">{author}</p>
                <p className="description">{description}</p>
                <p className="type">{type}</p>
                <div className="detailInfo">
                    <p className="price">
                        价格: <span className="book-price">¥{price}</span>{/*span支持变色*/}
                    </p>
                    {/*<p className="price">库存:{remain}</p>*/}
                    {/*<p className="price">销量:{sold}</p>*/}
                </div>
            </div>
            <img className="book-img" src={image} alt={title} />
        </div>
    );
}

export default Book;
