-- Drop the ecommerce Schema if it exists
DROP SCHEMA IF EXISTS ecommerce;

-- Create the ecommerce Schema
CREATE SCHEMA IF NOT EXISTS ecommerce;
USE ecommerce;

-- Drop Users Table if it exists
DROP TABLE IF EXISTS users;

-- Create Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop Addresses Table if it exists
DROP TABLE IF EXISTS addresses;

-- Create Addresses Table
CREATE TABLE addresses (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(50) NOT NULL,
    longitude DECIMAL(9,6),
    latitude DECIMAL(8,6),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Drop Products Table if it exists
DROP TABLE IF EXISTS products;

-- Create Products Table
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    uniq_id VARCHAR(255),
    crawl_timestamp TIMESTAMP,
    product_url TEXT,
    product_name TEXT NOT NULL,
    categories TEXT NOT NULL,
    pid VARCHAR(255),
    retail_price DECIMAL(10, 2) NOT NULL,
    discounted_price DECIMAL(10, 2) NOT NULL,
    image_urls JSON, -- Array of image URLs
    is_FK_Advantage_product BOOLEAN,
    product_description TEXT,
    product_rating varchar(2550),
    overall_rating varchar(2550),
    brand VARCHAR(1000),
    product_specifications TEXT,
    stock_quantity INT NOT NULL,
    quantity_unit VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Drop Cart Table if it exists
DROP TABLE IF EXISTS cart;

-- Create Cart Table
CREATE TABLE cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Drop Wishlist Table if it exists
DROP TABLE IF EXISTS wishlist;

-- Create Wishlist Table
CREATE TABLE wishlist (
    wishlist_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Drop Orders Table if it exists
DROP TABLE IF EXISTS orders;

-- Create Orders Table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Drop Order Items Table if it exists
DROP TABLE IF EXISTS order_items;

-- Create Order Items Table
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Drop Ratings Table if it exists
DROP TABLE IF EXISTS ratings;

-- Create Ratings Table
CREATE TABLE ratings (
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    rating INT NOT NULL,
    review TEXT, -- Text format for review
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Drop Rating Images Table if it exists
DROP TABLE IF EXISTS rating_images;

-- Create Rating Images Table
CREATE TABLE rating_images (
    image_id INT PRIMARY KEY AUTO_INCREMENT,
    rating_id INT,
    image LONGBLOB, -- Direct image data
    meta_data1 VARCHAR(255),
    meta_data2 VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rating_id) REFERENCES ratings(rating_id) ON DELETE CASCADE
);

-- Drop Cards Table if it exists
DROP TABLE IF EXISTS cards;

-- Create Cards Table
CREATE TABLE cards (
    card_id INT PRIMARY KEY AUTO_INCREMENT,
    card_number VARCHAR(16) NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    card_type VARCHAR(50) NOT NULL,
    expiration_date DATE NOT NULL,
    cvv INT NOT NULL,
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Drop Transactions Table if it exists
DROP TABLE IF EXISTS transactions;

-- Create Transactions Table
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    card_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (card_id) REFERENCES cards(card_id)
);

-- Drop Invoices Table if it exists
DROP TABLE IF EXISTS invoices;

-- Create Invoices Table
CREATE TABLE invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    transaction_id INT,
    payment_amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
);
