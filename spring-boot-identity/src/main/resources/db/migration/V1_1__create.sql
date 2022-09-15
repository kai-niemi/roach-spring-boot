drop type if exists account_type;

create type account_type as enum ('A', 'L', 'E', 'R', 'C');

create table account_uuid
(
    id             uuid         not null,
    balance        float        not null,
    currency       varchar(3)   not null default 'USD',
    name           varchar(128) null,
    description    varchar(256) null,
    type           account_type not null default 'A',
    closed         boolean      not null default false,
    allow_negative integer      not null default 0,
    creation_time  timestamptz  not null default clock_timestamp(),
    updated_time   timestamptz  null,

    primary key (id)
);

create table account_uuid_db
(
    id            uuid        not null default gen_random_uuid(),
    balance        float        not null,
    currency       varchar(3)   not null default 'USD',
    name           varchar(128) null,
    description    varchar(256) null,
    type           account_type not null default 'A',
    closed         boolean      not null default false,
    allow_negative integer      not null default 0,
    creation_time  timestamptz  not null default clock_timestamp(),
    updated_time   timestamptz  null,

    primary key (id)
);

create table account_numid
(
    id            int         not null,
    balance        float        not null,
    currency       varchar(3)   not null default 'USD',
    name           varchar(128) null,
    description    varchar(256) null,
    type           account_type not null default 'A',
    closed         boolean      not null default false,
    allow_negative integer      not null default 0,
    creation_time  timestamptz  not null default clock_timestamp(),
    updated_time   timestamptz  null,

    primary key (id)
);

create sequence if not exists account_seq increment by 50 cache 10;

create table account_sequence
(
    id            int         not null,
    balance        float        not null,
    currency       varchar(3)   not null default 'USD',
    name           varchar(128) null,
    description    varchar(256) null,
    type           account_type not null default 'A',
    closed         boolean      not null default false,
    allow_negative integer      not null default 0,
    creation_time  timestamptz  not null default clock_timestamp(),
    updated_time   timestamptz  null,

    primary key (id)
);
