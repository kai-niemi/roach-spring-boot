
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

-- alter table product
--     add constraint check_product_positive_inventory check (product.inventory >= 0);

