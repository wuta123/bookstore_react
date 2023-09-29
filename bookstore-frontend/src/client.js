import fetch from 'unfetch'

export const addNewItemToCart = async item => await fetch('/carts', {
    headers: {
        'Content-Type': 'application/json'
    },
    method: 'POST',
    body: JSON.stringify(item)
});

export const purchaseItem = async item => await fetch('/orders', {
    headers: {
        'Content-Type': 'application/json'
    },
    method: 'POST',
    body: JSON.stringify(item)
});

export const purchaseAllCartItem = async (carts) => {
    return await fetch('/orders/list', {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(carts)
    })
}

export const deleteOrder = async (order_id) => await fetch(`/orders/${order_id}`, {
    headers: {
        'Content-Type': 'application/json'
    },
    method: 'DELETE'
});

export const deleteCart = async (cart_id) => await fetch(`/carts/${cart_id}`, {
    headers: {
        'Content-Type': 'application/json'
    },
    method: 'DELETE'
});

export const deleteBook = async (id, book_id) => await fetch(`/books/delete?id=`+id+"&book_id="+book_id,{
    headers: {
        'Content-Type': 'application/json'
    },
    method: 'POST'
})

export const addBook = async bookAndId => await fetch(`/books/add`,{
    headers: {
        'Content-Type': 'application/json'
    },
    method: 'POST',
    body: JSON.stringify(bookAndId)
})

export const editBook = async bookAndId => await fetch(`/books/modify`, {
    headers:{
        'Content-Type' : 'application/json'
    },
    method: 'POST',
    body: JSON.stringify(bookAndId)
})

export const getBookById = async book_id => await fetch(`/books/getbyid?book_id=${book_id}`,{
    headers:{
        'Content-Type':'application/json'
    },
    method: 'GET',
})

export const getUserinfoById = async user_id => await fetch(`/users/getbyid?user_id=${user_id}`,{
    headers:{
        'Content-Type':'application/json'
    },
    method: 'GET',
})

export const checkAdmin = async (id, handleAdmin) => {
    try {
        const response = await fetch(`users/check?id=` + id, {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST',
        });

        const data = await response.json();
        console.log(data);
        handleAdmin(data);
    } catch (error) {
        console.log(error);
    }
};

