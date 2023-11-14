CREATE TABLE IF NOT EXISTS book2(
    id UUID PRIMARY KEY NOT NULL,
    title VARCHAR(100) NOT NULL,
    price DECIMAL NOT NULL,
    description VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    image VARCHAR(100) NOT NULL,
    remain INTEGER NOT NULL,
    sold INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_book_create_time ON book2 (create_time DESC);
CREATE INDEX idx_book_sold ON book2 (sold DESC);
CREATE INDEX idx_book_info ON book2 (SUBSTRING(author FROM 1 FOR 10) ASC, SUBSTRING(title FROM 1 FOR 10) ASC, price ASC);
