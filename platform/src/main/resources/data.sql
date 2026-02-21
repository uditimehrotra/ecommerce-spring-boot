-- Reset data for a clean test environment
TRUNCATE TABLE products CASCADE;

-- INSERT with the new stock_quantity column
INSERT INTO products (name, description, price, stock_quantity) 
VALUES ('Laptop', 'A powerful laptop for developers', 1200.00, 50);

INSERT INTO products (name, description, price, stock_quantity) 
VALUES ('Keyboard', 'A mechanical keyboard', 150.00, 100);

INSERT INTO products (name, description, price, stock_quantity) 
VALUES ('Mouse', 'An ergonomic wireless mouse', 75.50, 200);