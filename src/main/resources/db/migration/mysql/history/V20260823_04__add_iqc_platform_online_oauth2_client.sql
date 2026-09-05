SET NAMES utf8mb4;

-- IQC 同时支持本地开发和 OpenSabre 服务器部署两套 OAuth 客户端。
INSERT INTO oauth2_registered_client (
    id, client_id, client_id_issued_at, client_secret, client_secret_expires_at,
    client_name, client_authentication_methods, authorization_grant_types,
    redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings,
    deleted, created_time, updated_time, created_by, updated_by
)
SELECT
    'iqc-platform', 'iqc-platform', NOW(3), client_secret, client_secret_expires_at,
    'IQC 线上前端', client_authentication_methods, authorization_grant_types,
    'http://opensabre:3010/login/oauth2/code/iqc-platform',
    'http://opensabre:3010/login/oauth2/code/iqc-platform',
    scopes, client_settings, token_settings, 'N', NOW(3), NOW(3), 'system', 'system'
FROM oauth2_registered_client
WHERE client_id = 'iqc-platform-local'
  AND NOT EXISTS (
      SELECT 1 FROM oauth2_registered_client WHERE client_id = 'iqc-platform'
  );

UPDATE oauth2_registered_client
SET redirect_uris = 'http://opensabre:3010/login/oauth2/code/iqc-platform',
    post_logout_redirect_uris = 'http://opensabre:3010/login/oauth2/code/iqc-platform',
    updated_time = NOW(3),
    updated_by = 'system'
WHERE client_id = 'iqc-platform';
