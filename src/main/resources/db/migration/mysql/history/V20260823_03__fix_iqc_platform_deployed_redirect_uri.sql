-- 当前 IQC 站点通过浏览器 localhost:3010 访问，必须使用 IQC 自己的 OAuth 回调，不能复用 opensabre-admin 的回调。
UPDATE oauth2_registered_client
SET redirect_uris = 'http://localhost:3010/login/oauth2/code/iqc-platform-local',
    post_logout_redirect_uris = 'http://localhost:3010/login/oauth2/code/iqc-platform-local'
WHERE client_id = 'iqc-platform-local';
