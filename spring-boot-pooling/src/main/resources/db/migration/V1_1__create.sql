create table account
(
    id             uuid         not null,
    balance        float        not null,
    currency       varchar(3)   not null default 'USD',
    name           varchar(128) null,
    description    varchar(256) null,
    type           string       not null default 'A',
    closed         boolean      not null default false,
    allow_negative integer      not null default 0,
    creation_time  timestamptz  not null default clock_timestamp(),
    updated_time   timestamptz  null,

    primary key (id)
);
