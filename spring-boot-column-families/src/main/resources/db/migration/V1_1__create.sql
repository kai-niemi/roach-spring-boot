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

    family f1 (id),
    family f2 (bill_address1),
    family f3 (bill_address2),
    family f4 (bill_city),
    family f5 (bill_country),
    family f6 (bill_postcode),
    family f7 (bill_to_first_name),
    family f8 (bill_to_last_name),
    family f9 (date_placed),
    family f10 (deliv_to_first_name),
    family f11 (deliv_to_last_name),
    family f12 (deliv_address1),
    family f13 (deliv_address2),
    family f14 (deliv_city),
    family f15 (deliv_country),
    family f16 (deliv_postcode),
    family f17 (total_price),
    family f18 (order_status)
);

show create table purchase_order1;
show create table purchase_order2;