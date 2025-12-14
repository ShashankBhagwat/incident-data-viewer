CREATE TABLE employees (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100),
    dept VARCHAR(50)
);

CREATE TABLE products (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_name VARCHAR(100),
    price DECIMAL(10,2)
);

CREATE TABLE orders (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_date DATE,
    amount DECIMAL(10,2)
);

INSERT INTO employees(name, dept)
VALUES ('Shashank', 'IT'), ('Manasa', 'HR');

INSERT INTO products(product_name, price)
VALUES ('Laptop', 80000), ('Mouse', 1200);

INSERT INTO orders(order_date, amount)
VALUES ('2025-01-01', 5000), ('2025-01-02', 14000);