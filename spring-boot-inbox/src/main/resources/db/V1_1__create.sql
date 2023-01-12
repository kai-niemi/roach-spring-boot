CREATE SEQUENCE journal_seq START 1 INCREMENT 1;

-- Inbox table that stores events in json format, mapped to entity types via JPAs
-- single table inheritance model using event_type as discriminator.
CREATE TABLE journal
(
    id          uuid primary key as ((payload ->> 'id')::UUID) stored, -- Computed primary index column
    event_type  varchar(15) not null,
    status      varchar(64) not null,
    sequence_no int         default nextval('journal_seq'),
    payload     json,
    tag         varchar(64),
    updated_at  timestamptz default clock_timestamp(),

    INVERTED INDEX event_payload (payload)
);

CREATE INDEX idx_journal_main ON journal (event_type) STORING (status, sequence_no, payload, tag, updated_at);