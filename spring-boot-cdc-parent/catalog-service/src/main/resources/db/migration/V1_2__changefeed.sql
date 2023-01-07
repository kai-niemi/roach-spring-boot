-- SET CLUSTER SETTING kv.rangefeed.enabled = true;
-- SHOW CHANGEFEED JOBS;

CANCEL JOBS (SELECT job_id FROM [SHOW JOBS] where job_type = 'CHANGEFEED' and status = 'running');

CREATE CHANGEFEED INTO 'webhook-https://localhost:8443/order-service/cdc?insecure_tls_skip_verify=true'
WITH schema_change_policy='stop', key_in_value, updated, resolved='15s', webhook_sink_config='{"Flush": {"Messages": 10, "Frequency": "5s"}, "Retry": {"Max": "inf"}}'
AS SELECT
    cdc_updated_timestamp()::int AS event_timestamp,
    'v1' AS event_version,
    'product' AS event_table,
    IF(cdc_is_delete(),'delete',IF(cdc_prev()='null','create','update')) AS event_type,
    cdc_prev() as event_before,
    jsonb_build_object(
        'id', id,
        'name', name,
        'description', description,
        'price', concat(price::string, ' ', currency),
        'sku', sku,
        'inventory', inventory,
        'created_by', created_by,
        'created_at', created_at,
        'last_modified_by', last_modified_by,
        'last_modified_at', last_modified_at
    ) AS event_after
FROM product;

-- row_to_json(product.*)

SELECT job_id,description,num_runs,execution_errors FROM [SHOW JOBS] where job_type = 'CHANGEFEED' and status = 'running';

