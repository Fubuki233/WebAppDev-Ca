-- Reduce total_amount for all orders by a factor of 10
-- Run after verifying that this adjustment is intended
USE fashion;

START TRANSACTION;

ALTER TABLE Orders

UPDATE Orders SET total_amount = total_amount / 10;

COMMIT;

-- Optional verification query:
-- SELECT order_id, total_amount FROM Orders ORDER BY order_id;