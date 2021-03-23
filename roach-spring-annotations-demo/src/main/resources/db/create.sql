-- DROP TABLE IF EXISTS account cascade;

create table if not exists account
(
    id      bigint         not null primary key default unique_rowid(),
    balance numeric(19, 2) not null,
    name    varchar(128)   not null,
    type    varchar(25)    not null
);

create unique index if not exists idx_account on account (name, type);

truncate table account cascade;

insert into account (id, balance, name, type)
values (1, 500.00, 'alice', 'asset'),
       (2, 500.00, 'alice', 'expense'),
       (3, 500.00, 'bob', 'asset'),
       (4, 500.00, 'bob', 'expense');

