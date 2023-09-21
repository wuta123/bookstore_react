CREATE TABLE IF NOT EXISTS users(
--     直接函数依赖，依赖于id
--      不可分，满足第一范式
--      主键识别，满足第二范式
--      没有传递函数依赖，只有完全函数依赖，满足第三范式
    id UUID PRIMARY KEY NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    image VARCHAR(100) NOT NULL,
    role bool NOT NULL,
    cost DECIMAL NOT NULL
);
