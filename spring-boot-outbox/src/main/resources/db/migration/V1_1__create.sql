-- truncate table t_account;
-- truncate table t_transaction;
-- truncate table t_outbox;

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

alter table if exists t_transaction
    add constraint fk_transaction_ref_account
        foreign key (account_id)
            references t_account (id);

create index fk_account_id_ref_account_idx on t_transaction (account_id);

create table t_outbox
(
    id             uuid        not null default gen_random_uuid(),
    create_time    timestamptz not null default clock_timestamp(),
    aggregate_type string      not null,
    aggregate_id   string      null,
    event_type     string      not null,
    payload        jsonb       not null,

    primary key (id)
);

ALTER TABLE t_outbox SET (ttl_expire_after = '5 minutes', ttl_job_cron = '*/5 * * * *', ttl_select_batch_size = 256);
