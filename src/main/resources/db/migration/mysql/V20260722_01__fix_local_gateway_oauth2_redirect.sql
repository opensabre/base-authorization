-- 本地 OAuth2 客户端的回调路径必须与 Spring Security registrationId 一致。
UPDATE oauth2_registered_client
SET redirect_uris = 'http://localhost:3000/login/oauth2/code/base-gateway-local',
    post_logout_redirect_uris = 'http://localhost:3000/login/oauth2/code/base-gateway-local',
    updated_time = now(3),
    updated_by = 'system'
WHERE client_id = 'base-gateway-local'
  AND (redirect_uris <> 'http://localhost:3000/login/oauth2/code/base-gateway-local'
       OR post_logout_redirect_uris <> 'http://localhost:3000/login/oauth2/code/base-gateway-local');
