CREATE DATABASE IF NOT EXISTS product_service
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE product_service;

CREATE TABLE IF NOT EXISTS categories
(
    id                 VARCHAR(36)  NOT NULL PRIMARY KEY,
    name               VARCHAR(255) NULL,
    parent_id          VARCHAR(255) NULL,
    is_deleted         TINYINT(1)   NULL,
    created_date       DATETIME     NULL,
    created_by         VARCHAR(255) NULL,
    last_modified_date DATETIME     NULL,
    last_modified_by   VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS products
(
    id                 VARCHAR(36)  NOT NULL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    price              INT          NULL,
    stock              INT          NULL,
    category_id        VARCHAR(36)  NULL,
    is_deleted         TINYINT(1)   NULL,
    created_date       DATETIME     NULL,
    created_by         VARCHAR(255) NULL,
    last_modified_date DATETIME     NULL,
    last_modified_by   VARCHAR(255) NULL,
    CONSTRAINT products_lbfk_1 FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX category_id ON products (category_id);

INSERT IGNORE INTO categories (id, name, parent_id, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
('cat-001', 'Electronics', NULL, 0, NOW(), 'admin', NOW(), 'admin'),
('cat-002', 'Laptops', 'cat-001', 0, NOW(), 'admin', NOW(), 'admin'),
('cat-003', 'Accessories', 'cat-001', 0, NOW(), 'admin', NOW(), 'admin');

INSERT IGNORE INTO products (id, name, price, stock, category_id, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
('PROD-1001', 'Dell XPS 13', 1500, 10, 'cat-002', 0, NOW(), 'admin', NOW(), 'admin'),
('PROD-1002', 'Logitech Wireless Mouse', 25, 100, 'cat-003', 0, NOW(), 'admin', NOW(), 'admin'),
('PROD-2001', 'Samsung 24" Monitor', 250, 20, 'cat-002', 0, NOW(), 'admin', NOW(), 'admin'),
('PROD-3001', 'Apple AirPods Pro', 199, 50, 'cat-003', 0, NOW(), 'admin', NOW(), 'admin');

CREATE DATABASE IF NOT EXISTS promotion
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE promotion;

CREATE TABLE IF NOT EXISTS promotions
(
    id                 VARCHAR(36)  NOT NULL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    code               VARCHAR(50)  NOT NULL,
    discount_type      VARCHAR(20)  NOT NULL,
    discount_value     INT          NOT NULL,
    min_order_value    INT          DEFAULT 0,
    start_date         DATETIME     NULL,
    end_date           DATETIME     NULL,
    usage_limit        INT          NULL,
    is_deleted         TINYINT(1)   DEFAULT 0,
    created_date       DATETIME     NULL,
    created_by         VARCHAR(255) NULL,
    last_modified_date DATETIME     NULL,
    last_modified_by   VARCHAR(255) NULL,
    CONSTRAINT promotions_code_unique UNIQUE (code)
);

INSERT IGNORE INTO promotions (id, name, code, discount_type, discount_value, min_order_value, start_date, end_date, usage_limit, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
(UUID(), 'Giảm 10% cho đơn hàng trên 200k', 'SAVE10', 'PERCENT', 10, 200000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1000, 0, NOW(), 'admin', NOW(), 'admin'),
(UUID(), 'Giảm 50k cho đơn hàng trên 500k', 'V50K', 'AMOUNT', 50000, 500000, '2026-04-01 00:00:00', '2026-05-31 23:59:59', 500, 0, NOW(), 'admin', NOW(), 'admin'),
(UUID(), 'Giảm 20% toàn bộ đơn hàng', 'SUMMER20', 'PERCENT', 20, 0, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 100, 0, NOW(), 'admin', NOW(), 'admin'),
(UUID(), 'Khách mới giảm 15%', 'NEW15', 'PERCENT', 15, 100000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 200, 0, NOW(), 'admin', NOW(), 'admin'),
(UUID(), 'Black Friday - Giảm 100k', 'BF100', 'AMOUNT', 100000, 1000000, '2026-11-25 00:00:00', '2026-11-30 23:59:59', 5000, 0, NOW(), 'admin', NOW(), 'admin');

CREATE DATABASE IF NOT EXISTS order_service
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE order_service;

CREATE TABLE IF NOT EXISTS orders
(
    id                 VARCHAR(36)  NOT NULL PRIMARY KEY,
    customer_id        VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    total_amount       INT          NOT NULL,
    promotion_code     VARCHAR(255) NULL,
    is_deleted         TINYINT(1)   DEFAULT 0 NULL,
    created_date       TIMESTAMP(6) NULL,
    created_by         VARCHAR(255) NULL,
    last_modified_date TIMESTAMP(6) NULL,
    last_modified_by   VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS order_items
(
    id                 VARCHAR(36)  NOT NULL PRIMARY KEY,
    order_id           VARCHAR(36)  NOT NULL,
    product_id         VARCHAR(255) NOT NULL,
    price              INT          NOT NULL,
    quantity           INT          NOT NULL,
    is_deleted         TINYINT(1)   DEFAULT 0 NULL,
    created_date       TIMESTAMP(6) NULL,
    created_by         VARCHAR(255) NULL,
    last_modified_date TIMESTAMP(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

INSERT IGNORE INTO orders (id, customer_id, status, total_amount, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
('c3b75347-39bc-11f1-8cb6-3af2b4352b68', 'CUST001', 'NEW', 1000, 0, '2026-04-16 17:50:32.000000', NULL, NULL, NULL),
('c3b7c65b-39bc-11f1-8cb6-3af2b4352b68', 'CUST002', 'PAID', 2000, 0, '2026-04-16 17:50:32.000000', NULL, NULL, NULL),
('d4e8f2a0-1234-5678-9abc-def012345678', 'CUST003', 'PROCESSING', 150000, 0, NOW(), 'admin', NOW(), 'admin'),
('f9a8b7c6-5432-10fe-9876-543210fedcba', 'CUST004', 'COMPLETED', 299000, 0, NOW(), 'admin', NOW(), 'admin');

INSERT IGNORE INTO order_items (id, order_id, product_id, price, quantity, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
('item-001-1111-1111-1111-111111111111', 'c3b75347-39bc-11f1-8cb6-3af2b4352b68', 'PROD-1001', 500, 2, 0, NOW(), 'admin', NOW(), 'admin'),
('item-002-2222-2222-2222-222222222222', 'c3b7c65b-39bc-11f1-8cb6-3af2b4352b68', 'PROD-1002', 1000, 2, 0, NOW(), 'admin', NOW(), 'admin'),
('item-003-3333-3333-3333-333333333333', 'd4e8f2a0-1234-5678-9abc-def012345678', 'PROD-2001', 75000, 2, 0, NOW(), 'admin', NOW(), 'admin'),
('item-004-4444-4444-4444-444444444444', 'f9a8b7c6-5432-10fe-9876-543210fedcba', 'PROD-3001', 299000, 1, 0, NOW(), 'admin', NOW(), 'admin');
