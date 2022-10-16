-- truncate table account;
-- truncate table transaction;
-- truncate table outbox;

create table account
(
    id         int         not null default unordered_unique_rowid(),
    balance    float       not null,
    name       string      null,
    created_at timestamptz not null default clock_timestamp(),

    primary key (id)
);

create type transaction_status as enum ('placed', 'verified', 'cancelled');

create table transaction
(
    id         int                not null default unordered_unique_rowid(),
    account_id int                not null,
    amount     float              not null,
    type       string             not null default 'generic',
    status     transaction_status not null default 'placed',
    created_at timestamptz        not null default clock_timestamp(),

    primary key (id)
);

alter table if exists transaction
    add constraint fk_transaction_ref_account
        foreign key (account_id)
            references account (id);

create index fk_account_id_ref_account_idx on transaction (account_id);

create table poe_tag
(
    id             uuid        not null default gen_random_uuid(),
    created_at     timestamptz not null default clock_timestamp(),
    uri            string      not null,
    aggregate_type string      not null,
    body           jsonb       not null,

    primary key (id)
);

ALTER TABLE poe_tag SET
(
    ttl_expire_after = '5 minutes',
    ttl_job_cron = '*/5 * * * *',
    ttl_select_batch_size = 256
);
