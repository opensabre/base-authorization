-- Gateway sessions must remain refreshable after the two-hour access token expires.
UPDATE oauth2_registered_client
SET token_settings = JSON_SET(
        COALESCE(token_settings, JSON_OBJECT()),
        '$."settings.token.refresh-token-time-to-live"',
        2592000
    ),
    updated_time = CURRENT_TIMESTAMP,
    updated_by = 'migration'
WHERE client_id IN ('base-gateway', 'base-gateway-local', 'test_client1')
  AND (
      JSON_EXTRACT(token_settings, '$."settings.token.refresh-token-time-to-live"') IS NULL
      OR CAST(JSON_UNQUOTE(JSON_EXTRACT(
            token_settings,
            '$."settings.token.refresh-token-time-to-live"'
          )) AS DECIMAL(20, 3)) < 2592000
  );
