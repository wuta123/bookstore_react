import React, { useEffect, useState } from 'react';
import { Table, Avatar, Button, Modal } from 'antd';
import { LockOutlined, UnlockOutlined } from '@ant-design/icons';
import fetch from "unfetch";

const UserManage = function({ user_id }){
    const admin_id = user_id;
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modalVisible, setModalVisible] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);

    // Fetch users data from the server
    useEffect(() => {
        const fetchData = async () => {
            const result = await fetch('/users');
            const data = await result.json();
            setUsers(data);
            setLoading(data.isEmpty);
        };
        fetchData();
    }, []);

    const banUser = async (userId) => {
        await fetch(`/users/user/ban?user_id=${userId}&admin_id=${admin_id}`,{
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST'
        })
            .then(async (response) => {
                const data = await response.json();
                //console.log(data)
                return data;
            })
            .catch((error) => console.error("Error banning user:", error));
    };

    const unbanUser = async (userId) => {
        await fetch(`/users/user/unban?user_id=${userId}&admin_id=${admin_id}`,{
                headers: {
                    'Content-Type': 'application/json'
                },
                method: 'POST'
            }
            )
            .then(async (response) => {
                const data = await response.json();

                return data;
            })
            .catch((error) => console.error("Error unbanning user:", error));
    };


    const handleBanUnban = (user) => {
        setSelectedUser(user);
        setModalVisible(true);
    };

    // Handle modal confirmation
    const handleModalConfirm = () => {
        const userId = selectedUser.id;
        const isBanned = selectedUser.status === 0;

        // Call the corresponding API function based on user status
        if (isBanned) {
            unbanUser(userId).then(() => {
                const updatedUsers = users.map((user) =>
                    user.id === userId ? {...user, status: 1} : user
                );
                setUsers(updatedUsers);
                setModalVisible(false);
            });
        } else {
            banUser(userId).then((data) => {
                //console.log(data.msg);
                const updatedUsers = users.map((user) =>
                    user.id === userId ? {...user, status: 0} : user
                );
                setUsers(updatedUsers);
                setModalVisible(false);
            });
        }
    };

    const columns = [
        {
            title: 'Avatar',
            dataIndex: 'image',
            key: 'avatar',
            render: (avatar) => <Avatar src={avatar} />,
        },
        {
            title: 'Username',
            dataIndex: 'username',
            key: 'username',
        },
        {
            title: 'User ID',
            dataIndex: 'id',
            key: 'id',
        },
        {
            title: 'Email',
            dataIndex: 'email',
            key: 'email',
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
            render: (status) => (status === 1 ? 'Normal' : 'Banned'),
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, user) => (
                <Button
                    type={user.status === 1 ? 'primary' : 'danger'}
                    icon={user.status === 1 ? <LockOutlined /> : <UnlockOutlined />}
                    onClick={() => handleBanUnban(user)}
                    disabled={user.role} // Disable action button for admin users
                >
                    {user.status === 1 ? 'Ban' : 'Unban'}
                </Button>
            ),
        },
    ];

    return (
        <div>
            <h2>User Management</h2>
            <Table
                dataSource={users}
                columns={columns}
                loading={loading}
                rowKey="id"
            />
            <Modal
                open={modalVisible}
                title={`${selectedUser?.username} - ${
                    selectedUser?.status === 1 ? 'Ban' : 'Unban'
                } Confirmation`}
                onCancel={() => setModalVisible(false)}
                onOk={handleModalConfirm}
                okButtonProps={{ loading: loading }}
                cancelButtonProps={{ disabled: loading }}
            >
                <p>
                    Are you sure you want to{' '}
                    {selectedUser?.status === 1 ? 'ban' : 'unban'}{' '}
                    {selectedUser?.username}?
                </p>
            </Modal>
        </div>
    );
};

export default UserManage;

