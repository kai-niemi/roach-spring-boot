-- drop table product;
create table product
(
    id               uuid           not null default gen_random_uuid(),
    name             varchar(128)   not null,
    description      varchar(256),
    price            numeric(19, 2) not null,
    currency         varchar(3)     not null,
    sku              varchar(128)   not null unique,
    inventory        int            not null default 0,
    for_sale         boolean        not null default true,
    created_by       varchar(24),
    created_at       timestamptz    not null default clock_timestamp(),
    last_modified_by varchar(24),
    last_modified_at timestamptz,

    primary key (id)
);

alter table product
    add constraint check_product_positive_inventory check (product.inventory >= 0);
