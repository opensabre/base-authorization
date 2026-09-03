SET NAMES utf8mb4;

-- IQC 独立前端使用独立 registration，避免与 opensabre-admin 的 3000 回调互相覆盖。
INSERT INTO oauth2_registered_client (
    id, client_id, client_id_issued_at, client_secret, client_secret_expires_at,
    client_name, client_authentication_methods, authorization_grant_types,
    redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings,
    deleted, created_time, updated_time, created_by, updated_by
)
SELECT
    'iqc-platform-local', 'iqc-platform-local', NOW(3), client_secret, client_secret_expires_at,
    'IQC 本地前端', client_authentication_methods, authorization_grant_types,
    'http://localhost:3010/login/oauth2/code/iqc-platform-local',
    'http://localhost:3010/login/oauth2/code/iqc-platform-local',
    scopes, client_settings, token_settings, 'N', NOW(3), NOW(3), 'system', 'system'
FROM oauth2_registered_client
WHERE client_id = 'base-gateway-local'
  AND NOT EXISTS (
      SELECT 1 FROM oauth2_registered_client WHERE client_id = 'iqc-platform-local'
  );

UPDATE oauth2_registered_client
SET redirect_uris = 'http://localhost:3010/login/oauth2/code/iqc-platform-local',
    post_logout_redirect_uris = 'http://localhost:3010/login/oauth2/code/iqc-platform-local',
    updated_time = NOW(3),
    updated_by = 'system'
WHERE client_id = 'iqc-platform-local';
