CREATE TABLE IF NOT EXISTS cart (
       cart_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       user_id UUID NOT NULL,
       book_id UUID NOT NULL,
       quantity INTEGER NOT NULL,
       total_price DECIMAL(10,2) NOT NULL,
       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
       FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE
);

