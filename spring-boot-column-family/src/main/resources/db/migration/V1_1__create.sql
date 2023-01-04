drop table if exists purchase_order1;
drop table if exists purchase_order2;

create table purchase_order1
(
    id                  integer        not null default unordered_unique_rowid(),
    bill_address1       varchar(255),
    bill_address2       varchar(255),
    bill_city           varchar(255),
    bill_country        varchar(16),
    bill_postcode       varchar(16),
    bill_to_first_name  varchar(255),
    bill_to_last_name   varchar(255),
    date_placed         date           not null default current_date(),
    deliv_to_first_name varchar(255),
    deliv_to_last_name  varchar(255),
    deliv_address1      varchar(255),
    deliv_address2      varchar(255),
    deliv_city          varchar(255),
    deliv_country       varchar(16),
    deliv_postcode      varchar(16),
    order_status        varchar(64),
    total_price         decimal(18, 2) not null,

    primary key (id)
);

create table purchase_order2
(
    id                  integer        not null default unordered_unique_rowid(),
    bill_address1       varchar(255),
    bill_address2       varchar(255),
    bill_city           varchar(255),
    bill_country        varchar(16),
    bill_postcode       varchar(16),
    bill_to_first_name  varchar(255),
    bill_to_last_name   varchar(255),
    date_placed         date           not null default current_date(),
    deliv_to_first_name varchar(255),
    deliv_to_last_name  varchar(255),
    deliv_address1      varchar(255),
    deliv_address2      varchar(255),
    deliv_city          varchar(255),
    deliv_country       varchar(16),
    deliv_postcode      varchar(16),
    order_status        varchar(64),
    total_price         decimal(18, 2) not null,

    primary key (id),

    FAMILY f1 (id, bill_address1, bill_address2, bill_city, bill_country, bill_postcode, bill_to_first_name, bill_to_last_name, date_placed, deliv_to_first_name, deliv_to_last_name, deliv_address1, deliv_address2, deliv_city, deliv_country, deliv_postcode, order_status),
    family f2 (total_price)
);

show create table purchase_order1;
show create table purchase_order2;