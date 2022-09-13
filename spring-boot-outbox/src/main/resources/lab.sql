SET CLUSTER SETTING kv.rangefeed.enabled = true;

CREATE CHANGEFEED FOR TABLE t_outbox
    INTO 'webhook-https://localhost:8443/webhook?insecure_tls_skip_verify=true'
    WITH updated, resolved='15s',
        webhook_sink_config='{"Flush": {"Messages": 5, "Frequency": "1s"}, "Retry": {"Max": "inf"}}';

-- CANCEL JOBS (SELECT job_id FROM [SHOW JOBS] where job_type = 'CHANGEFEED');

SHOW CREATE TABLE t_outbox;
SHOW SCHEDULES;

WITH x AS (SHOW JOBS) SELECT * from x WHERE job_type = 'ROW LEVEL TTL';
ALTER TABLE t_outbox RESET (ttl_job_cron);

SELECT id, jsonb_pretty(payload) FROM t_outbox WHERE crdb_internal_expiration > now() limit 1;