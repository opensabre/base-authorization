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
    oidc_id_token_metadata        blob          DEFAULT NULL COMMENT 'oidc_id_token时间',
    refresh_token_value           blob          DEFAULT NULL COMMENT 'refresh_token原数据，java实例',
    refresh_token_issued_at       datetime      DEFAULT NULL COMMENT 'refresh_token生成时间',
    refresh_token_expires_at      datetime      DEFAULT NULL COMMENT 'refresh_token过期时间',
    refresh_token_metadata        blob          DEFAULT NULL COMMENT 'refresh_token原数据，java实例',
    PRIMARY KEY (id)
) COMMENT 'token记录表';

DROP TABLE IF EXISTS oauth2_authorization_consent;
CREATE TABLE oauth2_authorization_consent
(
    registered_client_id varchar(100) NOT NULL COMMENT 'client_id',
    principal_name       varchar(200) NOT NULL COMMENT '身份信息，一般为clientId',
    authorities          text         NOT NULL COMMENT '其他属性',
    PRIMARY KEY (registered_client_id, principal_name)
) COMMENT '授权记录';

DROP TABLE IF EXISTS oauth2_registered_client;
CREATE TABLE oauth2_registered_client
(
    id                            varchar(100)  NOT NULL COMMENT 'UUID生成',
    client_id                     varchar(100)  NOT NULL COMMENT 'client_id',
    client_id_issued_at           datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'client生成时间',
    client_secret                 varchar(200)           DEFAULT NULL COMMENT 'client密码',
    client_secret_expires_at      datetime               DEFAULT NULL COMMENT 'client密码过期时间',
    client_name                   varchar(200)  NOT NULL COMMENT 'client名称',
    client_authentication_methods varchar(1000) NOT NULL COMMENT '客户端支持的authentication_methods如：client_secret_basic、basic等',
    authorization_grant_types     varchar(1000) NOT NULL COMMENT '客户端支持的grant_type如：refresh_token,client_credentials,authorization_code等',
    redirect_uris                 varchar(1000)          DEFAULT NULL COMMENT '跳转url',
    scopes                        varchar(1000) NOT NULL COMMENT 'client支持的scope如:read、write等',
    client_settings               text          NOT NULL COMMENT 'client设置如：过期时间',
    token_settings                text          NOT NULL COMMENT 'token设置如：过期时间、类型等',
    deleted                       VARCHAR(1)    NOT NULL DEFAULT 'N' COMMENT '是否已删除Y：已删除，N：未删除',
    created_time                  DATETIME      NOT NULL DEFAULT now() COMMENT '创建时间',
    updated_time                  DATETIME      NOT NULL DEFAULT now() COMMENT '更新时间',
    created_by                    VARCHAR(100)  NOT NULL COMMENT '创建人',
    updated_by                    VARCHAR(100)  NOT NULL COMMENT '更新人',
    PRIMARY KEY (id)
) COMMENT 'client记录表';
