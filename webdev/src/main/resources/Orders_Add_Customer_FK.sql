-- Ensure Orders.customer_id references customer.customer_id
-- Run this script after verifying existing data has matching customer records.
START TRANSACTION;

-- Optional: make sure column type matches customer.customer_id (VARCHAR(36))
ALTER TABLE Orders
MODIFY COLUMN customer_id VARCHAR(36) NOT NULL;

-- Add an index to optimize lookups if it doesn't exist yet
ALTER TABLE Orders
ADD INDEX idx_orders_customer_id (customer_id);

-- Add the foreign key constraint (adjust constraint name if conflicts occur)
ALTER TABLE Orders
ADD CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON UPDATE CASCADE ON DELETE RESTRICT;

COMMIT;

-- If the constraint name already exists, drop it first:
-- ALTER TABLE Orders DROP FOREIGN KEY fk_orders_customer;