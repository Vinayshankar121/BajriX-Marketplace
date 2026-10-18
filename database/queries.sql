-- Run these queries in PostgreSQL if you want to understand the database manually.
-- Spring Boot creates the tables automatically when spring.jpa.hibernate.ddl-auto=create is used.

-- See all products.
SELECT *
FROM products;

-- See all sellers.
SELECT *
FROM sellers;

-- See products with every seller listing.
SELECT p.name AS product_name,
       s.name AS seller_name,
       s.status AS seller_status,
       sl.price,
       sl.stock,
       sl.minimum_order_quantity,
       sl.available
FROM seller_listings sl
JOIN products p ON p.id = sl.product_id
JOIN sellers s ON s.id = sl.seller_id
ORDER BY p.name, sl.price;

-- Buyer query: show only offers buyers can see.
SELECT s.name AS seller_name,
       sl.price,
       sl.stock,
       sl.minimum_order_quantity
FROM seller_listings sl
JOIN sellers s ON s.id = sl.seller_id
WHERE sl.product_id = 1
  AND s.status = 'APPROVED'
  AND sl.available = true
  AND sl.stock > 0
ORDER BY sl.price ASC;

-- Seller query: show listings only for seller 1.
SELECT sl.id,
       p.name AS product_name,
       sl.price,
       sl.stock,
       sl.minimum_order_quantity,
       sl.available
FROM seller_listings sl
JOIN products p ON p.id = sl.product_id
WHERE sl.seller_id = 1
ORDER BY p.name;

-- Add a listing manually.
INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (1, 3, 1200.00, 50, 1, true, 0);

-- Update price and stock for seller 1's listing.
UPDATE seller_listings
SET price = 395.00,
    stock = 450
WHERE id = 1
  AND seller_id = 1;

-- Stop seller 1 from selling a listing.
UPDATE seller_listings
SET available = false
WHERE id = 1
  AND seller_id = 1;

