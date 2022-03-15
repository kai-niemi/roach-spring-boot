create table customers
(
    id         uuid         not null default gen_random_uuid(),
    first_name varchar(45),
    last_name  varchar(45),
    user_name  varchar(128) not null unique,
    email      varchar(128) not null unique,
    address1   varchar(255) null,
    address2   varchar(255) null,
    postcode   varchar(16)  null,
    city       varchar(255) null,
    country    varchar(128) null,

    primary key (id)
);

create table order_items
(
    order_id   uuid           not null,
    product_id uuid           not null,
    quantity   int            not null,
    unit_price numeric(19, 2) not null,
    item_pos   int            not null,
    primary key (order_id, item_pos)
);

create type if not exists shipment_status as enum ('placed', 'confirmed', 'cancelled','delivered');

create table orders
(
    id             uuid            not null default gen_random_uuid(),
    customer_id    uuid            not null,
    total_price    numeric(19, 2)  not null,
    tags           string(128)     null,
    status         shipment_status not null default 'placed',
    date_placed    timestamptz     not null default clock_timestamp(),
    date_updated   timestamptz     not null default clock_timestamp(),

    deliv_address1 varchar(255)    null,
    deliv_address2 varchar(255)    null,
    deliv_postcode varchar(16)     null,
    deliv_city     varchar(255)    null,
    deliv_country  varchar(128)    null,

    primary key (id)
);

create table products
(
    id        uuid           not null default gen_random_uuid(),
    inventory int            not null,
    name      string(128)    not null,
    price     numeric(19, 2) not null,
    sku       string(128)    not null unique,
    primary key (id)
);

alter table products
    add constraint check_product_positive_inventory check (products.inventory >= 0);

alter table if exists order_items
    add constraint fk_order_item_ref_product
        foreign key (product_id)
            references products;

alter table if exists order_items
    add constraint fk_order_item_ref_order
        foreign key (order_id)
            references orders;

alter table if exists orders
    add constraint fk_order_ref_customer
        foreign key (customer_id)
            references customers;

-- Foreign key indexes
create index fk_order_item_ref_product_idx on order_items (product_id);
create index fk_order_ref_customer_idx on orders (customer_id);