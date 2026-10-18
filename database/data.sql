-- Run this after schema.sql.

INSERT INTO sellers (name, status)
VALUES ('Shree Traders', 'APPROVED');

INSERT INTO sellers (name, status)
VALUES ('Build Mart', 'APPROVED');

INSERT INTO sellers (name, status)
VALUES ('Future Supplies', 'PENDING');

INSERT INTO sellers (name, status)
VALUES ('Skyline Materials', 'REJECTED');

INSERT INTO sellers (name, status)
VALUES ('Urban Build Co.', 'APPROVED');

INSERT INTO products (name, category, unit, description)
VALUES ('UltraTech PPC Cement 50 kg', 'Cement', 'bag', 'Strong and durable PPC cement.');

INSERT INTO products (name, category, unit, description)
VALUES ('TMT Steel Bar 12 mm', 'Steel', 'piece', 'High strength reinforcement steel bar.');

INSERT INTO products (name, category, unit, description)
VALUES ('River Sand', 'Sand', 'ton', 'Clean river sand for construction.');

INSERT INTO products (name, category, unit, description)
VALUES ('Fly Ash Bricks', 'Brick', 'piece', 'Lightweight and durable masonry blocks.');

INSERT INTO products (name, category, unit, description)
VALUES ('Apex Exterior Paint', 'Paint', 'litre', 'Weather-resistant wall finish.');

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (1, 1, 390.00, 500, 10, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (2, 1, 385.00, 250, 20, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (3, 1, 375.00, 900, 10, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (1, 2, 680.00, 100, 5, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (2, 3, 1250.00, 0, 1, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (5, 3, 1180.00, 40, 1, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (1, 4, 12.00, 1200, 100, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (5, 5, 245.00, 320, 20, true, 0);

INSERT INTO seller_listings (seller_id, product_id, price, stock, minimum_order_quantity, available, version)
VALUES (4, 5, 260.00, 80, 15, false, 0);
