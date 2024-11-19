CREATE TABLE customers (
    id TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    name TEXT,
    hashed_password TEXT NOT NULL
);
