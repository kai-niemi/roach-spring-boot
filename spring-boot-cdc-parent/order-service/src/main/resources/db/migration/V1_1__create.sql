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

    primary key (id)
);

show create table product;