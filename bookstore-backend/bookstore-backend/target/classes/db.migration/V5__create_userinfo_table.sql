CREATE TABLE IF NOT EXISTS userinfo (
    user_id UUID NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    image VARCHAR(100) NOT NULL,
    status INTEGER NOT NULL,
    role bool NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

