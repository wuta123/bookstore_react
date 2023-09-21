import React, { useEffect, useState } from 'react';
import { Table, Input, DatePicker, Button } from 'antd';
import moment from 'moment';
import dayjs from 'dayjs';
import fetch from 'unfetch';
import { getBookById, getUserinfoById } from '../../client';

const { Search } = Input;
const { RangePicker } = DatePicker;

const ConsumeAnalytics = ({ user_id }) => {
    const admin_id = user_id;
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [usernames, setUsernames] = useState({});
    const [bookTitles, setBookTitles] = useState({});
    const [theFilteredOrders, setTheFilteredOrders] = useState([]);
    const [theFilteredUsers, setTheFilteredUsers] = useState([]);
    const [theFilteredBooks, setTheFilteredBooks] = useState([]);
    const [searchBookTitle, setSearchBookTitle] = useState('');
    const [searchPurchaseTimeRange, setSearchPurchaseTimeRange] = useState([]);
    const [checkUserBillboard, setCheckUserBillboard] = useState(false);
    const [checkBookBillboard, setCheckBookBillboard] = useState(false);
    const [checkAll, setCheckAll] = useState(true);
    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await fetch(`/orders?admin_id=${admin_id}`);
                const data = await response.json();
                if (data.msg === 'success') {
                    setOrders(data.data);
                    setTheFilteredOrders(data.data);
                    const userMap = {};
                    const bookMap = {};

                    // Calculate user consumption and book sales
                    data.data.forEach((order) => {
                        const { user_id, book_id, total_price, quantity } = order;

                        // Calculate user consumption
                        if (userMap[user_id]) {
                            userMap[user_id].total_cost += total_price;
                        } else {
                            userMap[user_id] = {
                                user_id,
                                total_cost: total_price,
                            };
                        }

                        // Calculate book sales
                        if (bookMap[book_id]) {
                            bookMap[book_id].total_quantity += quantity;
                        } else {
                            bookMap[book_id] = {
                                book_id,
                                total_quantity: quantity,
                            };
                        }
                    });

                    const theFilteredUsers = Object.values(userMap);
                    const theFilteredBooks = Object.values(bookMap);
                    const sortedUsers = [...theFilteredUsers].sort((a, b) => b.total_cost - a.total_cost);
                    const sortedBooks = [...theFilteredBooks].sort((a, b) => b.total_quantity - a.total_quantity);

                    setTheFilteredUsers(sortedUsers);
                    setTheFilteredBooks(sortedBooks);
                    setLoading(false);
                }
            } catch (error) {
                console.error(error);
            }
        };

        fetchOrders();
    }, [admin_id]);

    useEffect(() => {
        const fetchUsername = async (user_id) => {
            try {
                const response = await getUserinfoById(user_id);
                const data = await response.json();
                if (data.msg === 'success') {
                    setUsernames((prevUsernames) => ({
                        ...prevUsernames,
                        [user_id]: data.data.username,
                    }));
                }
            } catch (error) {
                console.error(error);
            }
        };

        const fetchBookTitle = async (book_id) => {
            try {
                const response = await getBookById(book_id);
                const data = await response.json();
                if (data.msg === 'success') {
                    setBookTitles((prevBookTitles) => ({
                        ...prevBookTitles,
                        [book_id]: data.data.title,
                    }));
                }
            } catch (error) {
                console.error(error);
            }
        };

        orders.forEach((order) => {
            const { user_id, book_id } = order;
            if (!usernames[user_id]) {
                fetchUsername(user_id);
            }
            if (!bookTitles[book_id]) {
                fetchBookTitle(book_id);
            }
        });
    }, [orders]);

    const handleSearch = () => {
        setLoading(true);

        const filteredOrders = orders.filter((order) => {
            const bookTitle = bookTitles[order.book_id];
            const purchaseTime = dayjs(order.purchase_time);

            // Filter by book title
            if (searchBookTitle && bookTitle) {
                const res1 = bookTitle.toLowerCase().includes(searchBookTitle.toLowerCase());
                if (res1) {
                    if (searchPurchaseTimeRange && searchPurchaseTimeRange.length === 2) {
                        const startDate = searchPurchaseTimeRange[0];
                        const endDate = searchPurchaseTimeRange[1];
                        const res2 =
                            (purchaseTime.isBefore(endDate, 'day') || purchaseTime.isSame(endDate, 'day')) &&
                            (purchaseTime.isAfter(startDate, 'day') || purchaseTime.isSame(startDate, 'day'));
                        return res2;
                    } else {
                        return res1;
                    }
                } else {
                    return false;
                }
            } else if (searchPurchaseTimeRange && searchPurchaseTimeRange.length === 2) {
                const startDate = searchPurchaseTimeRange[0];
                const endDate = searchPurchaseTimeRange[1];
                const res2 =
                    (purchaseTime.isBefore(endDate, 'day') || purchaseTime.isSame(endDate, 'day')) &&
                    (purchaseTime.isAfter(startDate, 'day') || purchaseTime.isSame(startDate, 'day'));
                return res2;
            } else {
                return true;
            }
        });
        setTheFilteredOrders(filteredOrders);

        const userMap = {};
        const bookMap = {};

        // Calculate user consumption and book sales
        filteredOrders.forEach((order) => {
            const { user_id, book_id, total_price, quantity } = order;

            // Calculate user consumption
            if (userMap[user_id]) {
                userMap[user_id].total_cost += total_price;
            } else {
                userMap[user_id] = {
                    user_id,
                    total_cost: total_price,
                };
            }

            // Calculate book sales
            if (bookMap[book_id]) {
                bookMap[book_id].total_quantity += quantity;
            } else {
                bookMap[book_id] = {
                    book_id,
                    total_quantity: quantity,
                };
            }
        });

        const theFilteredUsers = Object.values(userMap);
        const theFilteredBooks = Object.values(bookMap);
        const sortedUsers = [...theFilteredUsers].sort((a, b) => b.total_cost - a.total_cost);
        const sortedBooks = [...theFilteredBooks].sort((a, b) => b.total_quantity - a.total_quantity);

        setTheFilteredUsers(sortedUsers);
        setTheFilteredBooks(sortedBooks);

        setLoading(false);
    };

    const handleCheckUserBillboard = () => {
        setCheckUserBillboard(true);
        setCheckBookBillboard(false);
        setCheckAll(false);
    }

    const handleCheckBookBillboard = () => {
        setCheckBookBillboard(true);
        setCheckUserBillboard(false);
        setCheckAll(false);
    }

    const handleCheckAll = () => {
        setCheckAll(true);
        setCheckBookBillboard(false);
        setCheckUserBillboard(false);
    }

    const columnsAll = [
        {
            title: 'Order ID',
            dataIndex: 'order_id',
            key: 'order_id',
        },
        {
            title: 'Buyer',
            dataIndex: 'user_id',
            key: 'user_id',
            render: (user_id) => <span>{usernames[user_id] || 'Loading...'}</span>,
        },
        {
            title: 'Book Title',
            dataIndex: 'book_id',
            key: 'book_id',
            render: (book_id) => <span>{bookTitles[book_id] || 'Loading...'}</span>,
        },
        {
            title: 'Quantity',
            dataIndex: 'quantity',
            key: 'quantity',
        },
        {
            title: 'Total Price',
            dataIndex: 'total_price',
            key: 'total_price',
        },
        {
            title: 'Purchase Time',
            dataIndex: 'purchase_time',
            key: 'purchaseTime',
            render: (purchaseTime) => {
                const formattedTime = moment(purchaseTime).format('YYYY-MM-DD HH:mm:ss');
                return <span>{formattedTime}</span>;
            },
        },
    ];

    const columnsUser = [
        {
            title: 'User id',
            dataIndex: 'user_id',
            key: 'user_id',
        },
        {
            title: 'User',
            dataIndex: 'user_id',
            key: 'user_id',
            render: (user_id) => <span>{usernames[user_id] || 'Loading...'}</span>,
        },
        {
            title: 'Total Cost',
            dataIndex: 'total_cost',
            key: 'total_cost',
        },
    ]

    const columnsBook = [
        {
            title: 'Book id',
            dataIndex: 'book_id',
            key: 'book_id',
        },
        {
            title: 'Book Title',
            dataIndex: 'book_id',
            key: 'book_id',
            render: (book_id) => <span>{bookTitles[book_id] || 'Loading...'}</span>,
        },
        {
            title: 'Total Quantity',
            dataIndex: 'total_quantity',
            key: 'total_quantity',
        },
    ]

    return (
        <div>
            <h2>Consume Analytics</h2>
            <div>
                <Search
                    placeholder="Search by book title"
                    allowClear
                    onSearch={(value) => {
                        setSearchBookTitle(value);
                        handleSearch();
                    }}
                    onChange={(e) => setSearchBookTitle(e.target.value)}
                    style={{ width: 200, marginRight: 10 }}
                />
                <RangePicker
                    value={searchPurchaseTimeRange}
                    onChange={(dates) => setSearchPurchaseTimeRange(dates)}
                    style={{ marginRight: 10 }}
                />
                <Button onClick={handleSearch} style={{borderWidth:1, borderColor:'darkgreen'}}>Search</Button>
                <Button onClick={handleCheckAll} style={{borderWidth:1, borderColor:'darkgreen'}}>总览</Button>
                <Button onClick={handleCheckUserBillboard} style={{borderWidth:1, borderColor:'darkgreen'}}>用户消费榜</Button>
                <Button onClick={handleCheckBookBillboard} color='black' style={{borderWidth:1, borderColor:'darkgreen',color: 'black'}}>书籍畅销榜</Button>
            </div>
            {checkAll? (<Table dataSource={theFilteredOrders} columns={columnsAll} loading={loading} rowKey="order_id" />):(<></>)}
            {checkUserBillboard? (<Table dataSource={theFilteredUsers} columns={columnsUser} loading={loading} rowKey="user_id" />):(<></>)}
            {checkBookBillboard? (<Table dataSource={theFilteredBooks} columns={columnsBook} loading={loading} rowKey="book_id" />):(<></>)}
        </div>
    );
};

export default ConsumeAnalytics;
