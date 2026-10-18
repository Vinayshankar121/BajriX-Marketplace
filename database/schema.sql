-- Create the database once in pgAdmin or psql.
CREATE DATABASE bajrix_marketplace;

-- Connect to bajrix_marketplace before running the remaining SQL.

CREATE TABLE sellers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    unit VARCHAR(50),
    description VARCHAR(1000)
);

CREATE TABLE seller_listings (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    stock INTEGER NOT NULL,
    minimum_order_quantity INTEGER NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true,
    version BIGINT,
    CONSTRAINT fk_listing_seller FOREIGN KEY (seller_id) REFERENCES sellers(id),
    CONSTRAINT fk_listing_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT unique_seller_product UNIQUE (seller_id, product_id),
    CONSTRAINT valid_price CHECK (price > 0),
    CONSTRAINT valid_stock CHECK (stock >= 0),
    CONSTRAINT valid_minimum_order_quantity CHECK (minimum_order_quantity >= 1),
    CONSTRAINT valid_available_minimum_order_quantity CHECK (
        NOT available OR stock = 0 OR minimum_order_quantity <= stock
    )
);

CREATE INDEX index_product_name ON products(name);
CREATE INDEX index_listing_product_id ON seller_listings(product_id);
CREATE INDEX index_listing_seller_id ON seller_listings(seller_id);
