START TRANSACTION;

ALTER TABLE Orders
MODIFY COLUMN customer_id VARCHAR(36) NOT NULL;

ALTER TABLE Orders
ADD INDEX idx_orders_customer_id (customer_id);

ALTER TABLE Orders
ADD CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON UPDATE CASCADE ON DELETE RESTRICT;

COMMIT;

SELECT constraint_name
FROM information_schema.table_constraints
WHERE
    table_schema = 'fashion'
    AND table_name = 'Orders'
    AND constraint_type = 'FOREIGN KEY';
-- Update Orders.customer_id to the provided UUIDs
-- Mapping: ORD-001 -> first UUID, ORD-002 -> second UUID, ... ORD-050 -> 50th UUID
START TRANSACTION;

UPDATE Orders
SET
    customer_id = 'a682fdad-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-001';

UPDATE Orders
SET
    customer_id = 'a68362a0-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-002';

UPDATE Orders
SET
    customer_id = 'a683661a-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-003';

UPDATE Orders
SET
    customer_id = 'a6836713-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-004';

UPDATE Orders
SET
    customer_id = 'a683688c-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-005';

UPDATE Orders
SET
    customer_id = 'a68369cc-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-006';

UPDATE Orders
SET
    customer_id = 'a6836ae8-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-007';

UPDATE Orders
SET
    customer_id = 'a6836bba-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-008';

UPDATE Orders
SET
    customer_id = 'a6836d1e-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-009';

UPDATE Orders
SET
    customer_id = 'a6836df4-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-010';

UPDATE Orders
SET
    customer_id = 'a6836ec1-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-011';

UPDATE Orders
SET
    customer_id = 'a6836f97-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-012';

UPDATE Orders
SET
    customer_id = 'a6837071-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-013';

UPDATE Orders
SET
    customer_id = 'a6837151-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-014';

UPDATE Orders
SET
    customer_id = 'a6837217-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-015';

UPDATE Orders
SET
    customer_id = 'a68372de-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-016';

UPDATE Orders
SET
    customer_id = 'a68373be-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-017';

UPDATE Orders
SET
    customer_id = 'a683748b-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-018';

UPDATE Orders
SET
    customer_id = 'a683755d-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-019';

UPDATE Orders
SET
    customer_id = 'a6837635-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-020';

UPDATE Orders
SET
    customer_id = 'a683778a-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-021';

UPDATE Orders
SET
    customer_id = 'a683788b-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-022';

UPDATE Orders
SET
    customer_id = 'a6837948-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-023';

UPDATE Orders
SET
    customer_id = 'a6837a0a-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-024';

UPDATE Orders
SET
    customer_id = 'a6837dbd-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-025';

UPDATE Orders
SET
    customer_id = 'a6837eaf-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-026';

UPDATE Orders
SET
    customer_id = 'a6837f79-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-027';

UPDATE Orders
SET
    customer_id = 'a6838040-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-028';

UPDATE Orders
SET
    customer_id = 'a6838110-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-029';

UPDATE Orders
SET
    customer_id = 'a68381e4-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-030';

UPDATE Orders
SET
    customer_id = 'a68382d1-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-031';

UPDATE Orders
SET
    customer_id = 'a683839a-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-032';

UPDATE Orders
SET
    customer_id = 'a683846e-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-033';

UPDATE Orders
SET
    customer_id = 'a6838d83-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-034';

UPDATE Orders
SET
    customer_id = 'a6838fd6-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-035';

UPDATE Orders
SET
    customer_id = 'a68391cf-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-036';

UPDATE Orders
SET
    customer_id = 'a68392d5-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-037';

UPDATE Orders
SET
    customer_id = 'a68393d1-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-038';

UPDATE Orders
SET
    customer_id = 'a68394b1-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-039';

UPDATE Orders
SET
    customer_id = 'a683958a-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-040';

UPDATE Orders
SET
    customer_id = 'a68396ae-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-041';

UPDATE Orders
SET
    customer_id = 'a683977c-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-042';

UPDATE Orders
SET
    customer_id = 'a6839849-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-043';

UPDATE Orders
SET
    customer_id = 'a6839926-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-044';

UPDATE Orders
SET
    customer_id = 'a68399fe-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-045';

UPDATE Orders
SET
    customer_id = 'a6839ad0-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-046';

UPDATE Orders
SET
    customer_id = 'a6839b9d-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-047';

UPDATE Orders
SET
    customer_id = 'a6839c7c-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-048';

UPDATE Orders
SET
    customer_id = 'a6839d4a-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-049';

UPDATE Orders
SET
    customer_id = 'a6839ebe-a06d-11f0-a419-42010a400003'
WHERE
    order_id = 'ORD-050';

COMMIT;

-- Note: Ensure Orders table and referenced Customer rows exist before running.