--
-- Product schema for CockroachDB
--

-- Derived copy of the product table in the catalog service
create table product
(
    id               uuid           not null,
    name             varchar(128)   not null,
    description      varchar(256),
    price            numeric(19, 2) not null,
    currency         varchar(3)     not null,
    sku              varchar(128)   not null,
    inventory        int            not null,
    created_by       varchar(24),
    created_at       timestamptz    not null,
    last_modified_by varchar(24),
    last_modified_at timestamptz,

    primary key (id)
);

show create table product;