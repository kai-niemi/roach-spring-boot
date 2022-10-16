insert into account (id, balance, name)
select n,
       500.00 + random() * 500.00,
       md5(random()::text)
from generate_series(1, 5000) as n;
