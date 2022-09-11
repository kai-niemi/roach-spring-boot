create type account_type as enum ('A', 'L', 'E', 'R', 'C');

create table account_uuid
(
    id             uuid         not null,
    parent_id      uuid         null,

    balance        float        not null,
    currency       varchar(3)   not null default 'USD',
    name           varchar(128) null     default 'untitled',
    description    varchar(256) null,
    type           account_type not null default 'A',
    closed         boolean      not null default false,
    allow_negative integer      not null default 0,
    updated_time   timestamptz  not null default clock_timestamp(),
    creation_time  timestamptz  not null default clock_timestamp(),

    primary key (id)
);

alter table if exists account_uuid add constraint fk_account_to_parent
        foreign key (parent_id) references account_uuid (id);

create table account_uuid_db
(
    id            uuid        not null default gen_random_uuid(),
    balance       float       not null,
    creation_time timestamptz not null default clock_timestamp(),

    primary key (id)
);

create table account_numid
(
    id            int         not null,
    balance       float       not null,
    creation_time timestamptz not null default clock_timestamp(),

    primary key (id)
);

create sequence if not exists account_seq increment by 50 cache 10;

create table account_sequence
(
    id            int         not null,
    balance       float       not null,
    creation_time timestamptz not null default clock_timestamp(),

    primary key (id)
);
