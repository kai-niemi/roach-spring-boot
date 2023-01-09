-- Stores immutable events in json format, mapped to entity types via JPA's single table inheritance model
create table journal
(
    id         STRING PRIMARY KEY AS (payload ->> 'id') STORED, -- computed primary index column
    event_type varchar(15) not null, -- discriminator
    payload    json,
    tag        varchar(64),
    updated    timestamptz default clock_timestamp(),
    INVERTED INDEX event_payload (payload)
);

create index idx_journal_main on journal (event_type, tag) storing (payload);
