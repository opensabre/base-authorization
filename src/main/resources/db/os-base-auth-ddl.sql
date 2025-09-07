/*
IMPORTANT:
    If using PostgreSQL, update ALL columns defined with 'blob' to 'text',
    as PostgreSQL does not support the 'blob' data type.
*/
DROP TABLE IF EXISTS oauth2_authorization;
CREATE TABLE oauth2_authorization
(
    id                            varchar(100) NOT NULL COMMENT 'UUID生成',
    registered_client_id          varchar(100) NOT NULL COMMENT 'clientId',
    principal_name                varchar(200) NOT NULL COMMENT '身份信息，一般为clientId',
    authorization_grant_type      varchar(100) NOT NULL COMMENT '客户端支持的grant_type如：refresh_token,client_credentials,authorization_code等',
    authorized_scopes             varchar(1000) DEFAULT NULL COMMENT '客户',
    attributes                    blob          DEFAULT NULL COMMENT '其他属性',
    state                         varchar(500)  DEFAULT NULL COMMENT 'token状态',
    authorization_code_value      blob          DEFAULT NULL COMMENT '预授权码值',
    authorization_code_issued_at  datetime      DEFAULT NULL COMMENT '预授权码生成时间',
    authorization_code_expires_at datetime      DEFAULT NULL COMMENT '预授权码过期时间',
    authorization_code_metadata   blob          DEFAULT NULL COMMENT '预授权码原数据，java实例',
    access_token_value            blob          DEFAULT NULL COMMENT 'access_token值',
    access_token_issued_at        datetime      DEFAULT NULL COMMENT 'access_token生成时间',
    access_token_expires_at       datetime      DEFAULT NULL COMMENT 'access_token过期时间',
    access_token_metadata         blob          DEFAULT NULL COMMENT 'access_token原数据，java实例',
    access_token_type             varchar(100)  DEFAULT NULL COMMENT 'access_token类型如：Bearer',
    access_token_scopes           varchar(1000) DEFAULT NULL COMMENT 'access_token scopes如:read、write等',
    oidc_id_token_value           blob          DEFAULT NULL COMMENT 'oidc_id_token值',
    oidc_id_token_issued_at       datetime      DEFAULT NULL COMMENT 'oidc_id_token生成时间',
    oidc_id_token_expires_at      datetime      DEFAULT NULL COMMENT 'oidc_id_token过期时间',
    oidc_id_token_metadata        blob          DEFAULT NULL COMMENT 'oidc_id_token元数据',
    refresh_token_value           blob          DEFAULT NULL COMMENT 'refresh_token元数据，java实例',
    refresh_token_issued_at       datetime      DEFAULT NULL COMMENT 'refresh_token生成时间',
    refresh_token_expires_at      datetime      DEFAULT NULL COMMENT 'refresh_token过期时间',
    refresh_token_metadata        blob          DEFAULT NULL COMMENT 'refresh_token元数据，java实例',
    user_code_value               blob          DEFAULT NULL COMMENT '用户授权码',
    user_code_issued_at           datetime      DEFAULT NULL COMMENT '用户授权码生成时间',
    user_code_expires_at          datetime      DEFAULT NULL COMMENT '用户授权码过期时间',
    user_code_metadata            blob          DEFAULT NULL COMMENT '用户授权码元数据',
    device_code_value             blob          DEFAULT NULL COMMENT '设备授权码',
    device_code_issued_at         datetime      DEFAULT NULL COMMENT '设备授权码生成时间',
    device_code_expires_at        datetime      DEFAULT NULL COMMENT '设备授权码过期时间',
    device_code_metadata          blob          DEFAULT NULL COMMENT '设备授权码元数据',
    PRIMARY KEY (id)
) COMMENT 'token记录表';

DROP TABLE IF EXISTS oauth2_authorization_consent;
CREATE TABLE oauth2_authorization_consent
(
    registered_client_id varchar(100) NOT NULL COMMENT 'client_id',
    principal_name       varchar(200) NOT NULL COMMENT '身份信息，一般为clientId',
    authorities          text         NOT NULL COMMENT '授权记录',
    PRIMARY KEY (registered_client_id, principal_name)
) COMMENT '授权记录';

DROP TABLE IF EXISTS oauth2_registered_client;
CREATE TABLE oauth2_registered_client
(
    id                            varchar(100)  NOT NULL COMMENT 'UUID生成',
    client_id                     varchar(100)  NOT NULL COMMENT 'client_id',
    client_id_issued_at           datetime      NOT NULL DEFAULT now() COMMENT 'client生成时间',
    client_secret                 varchar(200)           DEFAULT NULL COMMENT 'client密码',
    client_secret_expires_at      datetime               DEFAULT NULL COMMENT 'client密码过期时间',
    client_name                   varchar(200)  NOT NULL COMMENT 'client名称',
    client_authentication_methods varchar(1000) NOT NULL COMMENT '客户端支持的authentication_methods如：client_secret_basic、basic等',
    authorization_grant_types     varchar(1000) NOT NULL COMMENT '客户端支持的grant_type如：refresh_token,client_credentials,authorization_code等',
    redirect_uris                 varchar(1000)          DEFAULT NULL COMMENT '跳转url',
    post_logout_redirect_uris     varchar(1000)          DEFAULT NULL COMMENT '注销url',
    scopes                        varchar(1000) NOT NULL COMMENT 'client支持的scope如:read、write等',
    client_settings               text          NOT NULL COMMENT 'client设置如：过期时间',
    token_settings                text          NOT NULL COMMENT 'token设置如：过期时间、类型等',
    deleted                       varchar(1)    NOT NULL DEFAULT 'N' COMMENT '是否已删除Y：已删除，N：未删除',
    created_time                  datetime      NOT NULL DEFAULT now() COMMENT '创建时间',
    updated_time                  datetime      NOT NULL DEFAULT now() COMMENT '更新时间',
    created_by                    varchar(100)  NOT NULL COMMENT '创建人',
    updated_by                    varchar(100)  NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) COMMENT 'client记录表';
CREATE UNIQUE INDEX ux_client_id ON oauth2_registered_client (client_id);

INSERT INTO `oauth2_registered_client` VALUES ('1759610473724018690','test_client2','2024-02-20 00:06:43','$2a$10$At0g9cWs/kb.od3qX/WvRezgfJ5rfZNmWbfWUYHGpu13HLDvrDDVG','2024-12-20 02:06:40','test','client_secret_basic','client_credentials,authorization_code','https://www.baidu.com','https://www.baidu.com','read','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":300.000000000,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600.000000000}','N','2024-02-20 00:06:44','2024-02-20 00:06:44','system','system'),
                                              ('1759611655041441793','test_client1','2024-02-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2025-12-20 02:11:21','test','client_secret_basic','client_credentials,authorization_code,refresh_token','http://www.opensabre.cloud:8443/login/oauth2/code/test_client1-client','http://www.opensabre.cloud:8443/login/oauth2/code/test_client1-client','read,openid,profile','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":7200,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600}','N','2024-02-20 00:11:25','2024-02-20 00:11:25','system','system'),
                                              ('1759611655041449999','device-message-client','2024-02-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2024-12-20 02:11:21','设备码授权客户端','none','urn:ietf:params:oauth:grant-type:device_code,refresh_token','','','read,write','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":300.000000000,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600.000000000}','N','2024-02-20 00:11:25','2024-02-20 00:11:25','system','system'),
                                              ('1759611655061449999','pkce-message-client','2024-02-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2024-12-20 02:11:21','PKCE客户端','none','authorization_code,refresh_token','http://127.0.0.1:8000/login/oauth2/code/messaging-client-oidc','http://127.0.0.1:8000/login/oauth2/code/messaging-client-oidc','read,write','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":300.000000000,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600.000000000}','N','2024-02-20 00:11:25','2024-02-20 00:11:25','system','system');

