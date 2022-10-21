create table product
(
    id        uuid           not null default gen_random_uuid(),
    version   int            not null default 0,
    inventory int            not null,
    name      varchar(128)   not null,
    price     numeric(19, 2) not null,
    sku       varchar(128)   not null unique,

    primary key (id)
);

insert into product (inventory,name,price,sku)
select 10 + random() * 50,
       md5(random()::text),
       500.00 + random() * 500.00,
       gen_random_uuid()::text
from generate_series(1, 1500) as i;

-- select * from product limit 10;

create table shedlock
(
    name       varchar(64)  not null,
    lock_until timestamp    not null,
    locked_at  timestamp    not null,
    locked_by  varchar(255) not null,
    primary key (name)
);

-- select * from shedlock;