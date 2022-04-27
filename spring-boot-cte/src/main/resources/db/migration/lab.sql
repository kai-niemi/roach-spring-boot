insert into t_account (id,balance)
select i,
       500.00
from generate_series(1, 1000) as i;

WITH x AS (
    INSERT INTO t_transaction (
                               account_id,
                               amount,
                               transaction_type,
                               transaction_status)
        VALUES (1, '100.00', 'test', 'hello') RETURNING account_id)
UPDATE t_account
SET balance=balance + 100.00
WHERE id = 1
RETURNING NOTHING;

WITH x AS (
    INSERT INTO t_transaction (
                               account_id,
                               amount,
                               transaction_type,
                               transaction_status)
        VALUES (?, ?, ?, ?) RETURNING account_id)
UPDATE t_account
SET balance=balance+?
WHERE id=?
RETURNING NOTHING;