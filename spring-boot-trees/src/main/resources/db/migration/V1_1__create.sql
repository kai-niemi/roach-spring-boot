create table category
(
    id            int          not null default unordered_unique_rowid(),
    name          varchar(64)  not null,
    description   varchar(256),
    category_type varchar(32)  not null,
    lft           int          not null,
    rgt           int          not null,
    parent_id     int,

    country       varchar(128) null, -- Region sub-category

    primary key (id)
);

create table categorized_product
(
    category_id int not null,
    product_id  int not null,
    expires_at  timestamptz,

    primary key (category_id, product_id)
);

create table product
(
    id          int          not null default unordered_unique_rowid(),
    name        varchar(128) not null,
    sku_code    varchar(128) not null,
    description varchar(512),

    primary key (id)
);

create table product_tag
(
    product_id int         not null,
    name       varchar(64) not null,

    primary key (product_id, name)
);

create table product_variation
(
    id         int         not null default unordered_unique_rowid(),
    list_price numeric(19, 2),
    currency   varchar(255),
    sku_code   varchar(24) not null,
    product_id int         not null,

    primary key (id)
);

create table product_variation_attribute
(
    product_variation_id int          not null,
    name                 varchar(64)  not null,
    value                varchar(256) not null,

    primary key (product_variation_id, name)
);

alter table if exists category
    add constraint uidx_category_name unique (name, parent_id);

alter table if exists product
    add constraint uidx_product_sku unique (sku_code);

alter table if exists product_variation
    add constraint uidx_product_variation_sku unique (sku_code);

alter table if exists categorized_product
    add constraint fk_categorized_product_product
        foreign key (product_id)
            references product;

alter table if exists categorized_product
    add constraint fk_categorized_product_category
        foreign key (category_id)
            references category;

alter table if exists category
    add constraint fk_category_parent
        foreign key (parent_id)
            references category;

alter table if exists product_tag
    add constraint fk_product_tag_product
        foreign key (product_id)
            references product;

alter table if exists product_variation
    add constraint fk_product_variation_product
        foreign key (product_id)
            references product;

alter table if exists product_variation_attribute
    add constraint fk_product_variation_attr_variation
        foreign key (product_variation_id)
            references product_variation;
