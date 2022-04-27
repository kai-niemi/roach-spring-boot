-- truncate table t_account;
-- truncate table t_transaction;
-- show create table t_account;
-- drop table t_account cascade;

create table t_account
(
    id            int         not null default unique_rowid(),
    balance       float       not null,
    creation_time timestamptz not null default clock_timestamp(),

    primary key (id)
);

create table t_transaction
(
    id                 int         not null default unique_rowid(),
    account_id         int         not null,
    amount             float       not null,
    transaction_type   string      not null default 'generic',
    transaction_status string      not null default 'pending',
    creation_time      timestamptz not null default clock_timestamp(),

    primary key (id)
);

-- alter table if exists t_transaction
--     add constraint fk_transaction_ref_account
--         foreign key (account_id)
--             references t_account (id);

