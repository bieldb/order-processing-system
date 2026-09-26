CREATE TABLE customers (
    id    UUID         PRIMARY KEY,
    name  VARCHAR(80)  NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT uk_customers_email UNIQUE (email)
);

CREATE TABLE products (
    id    UUID           PRIMARY KEY,
    name  VARCHAR(255)   NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    stock INTEGER        NOT NULL,
    CONSTRAINT ck_products_price_non_negative CHECK (price >= 0),
    CONSTRAINT ck_products_stock_non_negative CHECK (stock >= 0)
);

CREATE TABLE orders (
    id          UUID           PRIMARY KEY,
    customer_id UUID           NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    total       NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT ck_orders_status CHECK (status IN ('CREATED', 'PROCESSING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_orders_total_non_negative CHECK (total >= 0)
);

CREATE TABLE order_items (
    id         UUID           PRIMARY KEY,
    order_id   UUID           NOT NULL,
    product_id UUID           NOT NULL,
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    subtotal   NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT ck_order_items_quantity_positive CHECK (quantity > 0),
    CONSTRAINT ck_order_items_unit_price_non_negative CHECK (unit_price >= 0),
    CONSTRAINT ck_order_items_subtotal_non_negative CHECK (subtotal >= 0)
);

-- O PostgreSQL não cria índices automaticamente para foreign keys.
CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
