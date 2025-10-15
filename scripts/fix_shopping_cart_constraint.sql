-- Fix Shopping Cart Table Constraints
-- This script removes the old unique constraint that only includes (customer_id, product_id)
-- and keeps the new constraint that includes (customer_id, product_id, sku)
--
-- Issue: The old constraint prevents users from adding the same product with different SKUs
-- Solution: Drop the old constraint, keep only the (customer_id, product_id, sku) constraint
--
-- Date: 2025-10-15
-- Author: GitHub Copilot

USE aori;

-- Drop the old unique constraint that causes duplicate entry errors
ALTER TABLE shopping_cart DROP INDEX customer_id;

-- Verify the remaining constraints
SHOW CREATE TABLE shopping_cart;