CREATE TABLE IF NOT EXISTS book(
    book_id UUID PRIMARY KEY NOT NULL,
    title VARCHAR(100) NOT NULL,
    price VARCHAR(100) NOT NULL,
    description VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    image VARCHAR(100) NOT NULL,
    remain INTEGER NOT NULL,
    sold INTEGER NOT NULL
);
