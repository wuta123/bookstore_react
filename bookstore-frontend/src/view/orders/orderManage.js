import React, { useEffect, useState } from 'react';
import { Table, Input, DatePicker, Button } from 'antd';
import moment from 'moment';
import dayjs from 'dayjs';
import fetch from 'unfetch';
import { getBookById, getUserinfoById } from '../../client';

const { Search } = Input;
const { RangePicker } = DatePicker;

const OrderManagement = ({ user_id }) => {
    const admin_id = user_id;
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [usernames, setUsernames] = useState({});
    const [bookTitles, setBookTitles] = useState({});
    const [theFilteredOrders, setTheFilteredOrders] = useState([]);
    const [searchBookTitle, setSearchBookTitle] = useState('');
    const [searchPurchaseTimeRange, setSearchPurchaseTimeRange] = useState([]);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await fetch(`/orders?admin_id=${admin_id}`);
                const data = await response.json();
                if (data.msg === 'success') {
                    setOrders(data.data);
                    setTheFilteredOrders(data.data);
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
                if(res1){
                    if (searchPurchaseTimeRange && searchPurchaseTimeRange.length === 2) {
                        const startDate = searchPurchaseTimeRange[0];
                        const endDate = searchPurchaseTimeRange[1];
                        const res2 =  (purchaseTime.isBefore(endDate,'day') || purchaseTime.isSame(endDate,'day'))
                            && (purchaseTime.isAfter(startDate,'day') || purchaseTime.isSame(startDate,'day'));
                        return res2;
                    }else{
                        return res1;
                    }
                } else {
                    return false;
                }
            } else if (searchPurchaseTimeRange && searchPurchaseTimeRange.length === 2){
                const startDate = searchPurchaseTimeRange[0];
                const endDate = searchPurchaseTimeRange[1];
                const res2 =  (purchaseTime.isBefore(endDate,'day') || purchaseTime.isSame(endDate,'day'))
                    && (purchaseTime.isAfter(startDate,'day') || purchaseTime.isSame(startDate,'day'));
                return res2;
            } else {
                return true;
            }
        });

        setTheFilteredOrders(filteredOrders);
        setLoading(false);
    };


    const columns = [
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

    return (
        <div>
            <h2>Order Management</h2>
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
                <Button type="primary" onClick={handleSearch}>Search</Button>

            </div>
            <Table dataSource={theFilteredOrders} columns={columns} loading={loading} rowKey="order_id" />
        </div>
    );
};

export default OrderManagement;
