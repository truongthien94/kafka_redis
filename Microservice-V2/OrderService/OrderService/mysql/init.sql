-- =============================
-- CREATE DATABASE
-- =============================
DROP DATABASE IF EXISTS order_service;
CREATE DATABASE order_service;
USE order_service;

-- =============================
-- TABLE: orders
-- =============================
-- Tạo bảng orders
CREATE TABLE orders
(
    id                  VARCHAR(36)   NOT NULL PRIMARY KEY,
    customer_id         VARCHAR(255)  NOT NULL,
    status              VARCHAR(255)  NOT NULL,
    total_amount        INT           NOT NULL,
    is_deleted          TINYINT(1)    DEFAULT 0 NULL,
    created_date        TIMESTAMP(6)  NULL,
    created_by          VARCHAR(255)  NULL,
    last_modified_date  TIMESTAMP(6)  NULL,
    last_modified_by    VARCHAR(255)  NULL
);

-- Tạo bảng order_items
CREATE TABLE order_items
(
    id                  VARCHAR(36)   NOT NULL PRIMARY KEY,
    order_id            VARCHAR(36)   NOT NULL,
    product_id          VARCHAR(255)  NOT NULL,
    price               INT           NOT NULL,
    quantity            INT           NOT NULL,
    is_deleted          TINYINT(1)    DEFAULT 0 NULL,
    created_date        TIMESTAMP(6)  NULL,
    created_by          VARCHAR(255)  NULL,
    last_modified_date  TIMESTAMP(6)  NULL,
    last_modified_by    VARCHAR(255)  NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

-- Insert dữ liệu mẫu cho orders (dùng UUID)
INSERT INTO orders (id, customer_id, status, total_amount, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
('c3b75347-39bc-11f1-8cb6-3af2b4352b68', 'CUST001', 'NEW', 1000, 0, '2026-04-16 17:50:32.000000', NULL, NULL, NULL),
('c3b7c65b-39bc-11f1-8cb6-3af2b4352b68', 'CUST002', 'PAID', 2000, 0, '2026-04-16 17:50:32.000000', NULL, NULL, NULL),
('d4e8f2a0-1234-5678-9abc-def012345678', 'CUST003', 'PROCESSING', 150000, 0, NOW(), 'admin', NOW(), 'admin'),
('f9a8b7c6-5432-10fe-9876-543210fedcba', 'CUST004', 'COMPLETED', 299000, 0, NOW(), 'admin', NOW(), 'admin');

-- Insert dữ liệu mẫu cho order_items (liên kết với orders.id)
INSERT INTO order_items (id, order_id, product_id, price, quantity, is_deleted, created_date, created_by, last_modified_date, last_modified_by) VALUES
('item-001-1111-1111-1111-111111111111', 'c3b75347-39bc-11f1-8cb6-3af2b4352b68', 'PROD-1001', 500, 2, 0, NOW(), 'admin', NOW(), 'admin'),
('item-002-2222-2222-2222-222222222222', 'c3b7c65b-39bc-11f1-8cb6-3af2b4352b68', 'PROD-1002', 1000, 2, 0, NOW(), 'admin', NOW(), 'admin'),
('item-003-3333-3333-3333-333333333333', 'd4e8f2a0-1234-5678-9abc-def012345678', 'PROD-2001', 75000, 2, 0, NOW(), 'admin', NOW(), 'admin'),
('item-004-4444-4444-4444-444444444444', 'f9a8b7c6-5432-10fe-9876-543210fedcba', 'PROD-3001', 299000, 1, 0, NOW(), 'admin', NOW(), 'admin');