explain WITH x AS (SELECT payload from journal where event_type = 'TRANSACTION' AND tag='cashout'),
     items AS (SELECT json_array_elements(payload -> 'items') as y FROM x)
SELECT sum((y ->> 'amount')::decimal)
FROM items;

