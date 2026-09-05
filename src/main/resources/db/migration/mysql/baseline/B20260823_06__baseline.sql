-- Generated from the complete verified migration history.
-- Regenerate with base-k8s/scripts/generate-flyway-baselines.sh; do not edit manually.

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `oauth2_authorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth2_authorization` (
  `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID生成',
  `registered_client_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'clientId',
  `principal_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份信息，一般为clientId',
  `authorization_grant_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端支持的grant_type如：refresh_token,client_credentials,authorization_code等',
  `authorized_scopes` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户',
  `attributes` blob COMMENT '其他属性',
  `state` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'token状态',
  `authorization_code_value` blob COMMENT '预授权码值',
  `authorization_code_issued_at` datetime DEFAULT NULL COMMENT '预授权码生成时间',
  `authorization_code_expires_at` datetime DEFAULT NULL COMMENT '预授权码过期时间',
  `authorization_code_metadata` blob COMMENT '预授权码原数据，java实例',
  `access_token_value` blob COMMENT 'access_token值',
  `access_token_issued_at` datetime DEFAULT NULL COMMENT 'access_token生成时间',
  `access_token_expires_at` datetime DEFAULT NULL COMMENT 'access_token过期时间',
  `access_token_metadata` blob COMMENT 'access_token原数据，java实例',
  `access_token_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'access_token类型如：Bearer',
  `access_token_scopes` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'access_token scopes如:read、write等',
  `oidc_id_token_value` blob COMMENT 'oidc_id_token值',
  `oidc_id_token_issued_at` datetime DEFAULT NULL COMMENT 'oidc_id_token生成时间',
  `oidc_id_token_expires_at` datetime DEFAULT NULL COMMENT 'oidc_id_token过期时间',
  `oidc_id_token_metadata` blob COMMENT 'oidc_id_token元数据',
  `refresh_token_value` blob COMMENT 'refresh_token元数据，java实例',
  `refresh_token_issued_at` datetime DEFAULT NULL COMMENT 'refresh_token生成时间',
  `refresh_token_expires_at` datetime DEFAULT NULL COMMENT 'refresh_token过期时间',
  `refresh_token_metadata` blob COMMENT 'refresh_token元数据，java实例',
  `user_code_value` blob COMMENT '用户授权码',
  `user_code_issued_at` datetime DEFAULT NULL COMMENT '用户授权码生成时间',
  `user_code_expires_at` datetime DEFAULT NULL COMMENT '用户授权码过期时间',
  `user_code_metadata` blob COMMENT '用户授权码元数据',
  `device_code_value` blob COMMENT '设备授权码',
  `device_code_issued_at` datetime DEFAULT NULL COMMENT '设备授权码生成时间',
  `device_code_expires_at` datetime DEFAULT NULL COMMENT '设备授权码过期时间',
  `device_code_metadata` blob COMMENT '设备授权码元数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='token记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `oauth2_authorization_consent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth2_authorization_consent` (
  `registered_client_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'client_id',
  `principal_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份信息，一般为clientId',
  `authorities` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '授权记录',
  PRIMARY KEY (`registered_client_id`,`principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='授权记录';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `oauth2_registered_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth2_registered_client` (
  `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID生成',
  `client_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'client_id',
  `client_id_issued_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'client生成时间',
  `client_secret` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'client密码',
  `client_secret_expires_at` datetime DEFAULT NULL COMMENT 'client密码过期时间',
  `client_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'client名称',
  `client_authentication_methods` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端支持的authentication_methods如：client_secret_basic、basic等',
  `authorization_grant_types` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端支持的grant_type如：refresh_token,client_credentials,authorization_code等',
  `redirect_uris` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转url',
  `post_logout_redirect_uris` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注销url',
  `scopes` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'client支持的scope如:read、write等',
  `client_settings` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'client设置如：过期时间',
  `token_settings` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'token设置如：过期时间、类型等',
  `deleted` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '是否已删除Y：已删除，N：未删除',
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `created_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `updated_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='client记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `oauth2_registered_client` VALUES ('1759610473724018690','test_client2','2024-02-20 00:06:43','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2030-12-20 02:06:40','test','client_secret_basic','client_credentials,authorization_code','https://www.baidu.com','https://www.baidu.com','read','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":300.0,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600.0}','N','2024-02-20 00:06:44.000','2024-02-20 00:06:44.000','system','system'),('1759611655041441793','test_client1','2024-02-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2030-12-20 02:11:21','test','client_secret_basic','client_credentials,authorization_code,refresh_token','http://localhost:8443/login/oauth2/code/base-gateway-client','http://localhost:8443/login/oauth2/code/base-gateway-client','read,openid,profile','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.access-token-format\": {\"value\": \"self-contained\"}, \"settings.token.reuse-refresh-tokens\": true, \"settings.token.access-token-time-to-live\": 7200, \"settings.token.refresh-token-time-to-live\": 2592000, \"settings.token.id-token-signature-algorithm\": \"RS256\"}','N','2024-02-20 00:11:25.000','2026-09-04 05:00:23.000','system','migration'),('1759611655041441794','base-gateway','2025-11-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2030-12-20 02:11:21','opensabre网关','client_secret_basic','client_credentials,authorization_code,refresh_token','http://opensabre:8080/login/oauth2/code/base-gateway-client','http://opensabre:8080/login/oauth2/code/base-gateway-client','read,openid,profile','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.access-token-format\": {\"value\": \"self-contained\"}, \"settings.token.reuse-refresh-tokens\": true, \"settings.token.access-token-time-to-live\": 7200.0, \"settings.token.refresh-token-time-to-live\": 2592000, \"settings.token.id-token-signature-algorithm\": \"RS256\"}','N','2025-11-20 00:11:25.000','2026-09-04 05:00:23.000','system','migration'),('1759611655041449998','device-message-client','2024-02-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2030-12-20 02:11:21','设备码授权客户端','none','urn:ietf:params:oauth:grant-type:device_code,refresh_token','','','read,write','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":300.0,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600.0}','N','2024-02-20 00:11:25.000','2024-02-20 00:11:25.000','system','system'),('1759611655061449999','pkce-message-client','2024-02-20 00:11:25','$2a$10$Nq7LumxAsVmEqQY.0cK.eu2sh1elC4ktmgfG37Syl3Opop0zT/4dC','2030-12-20 02:11:21','PKCE客户端','none','authorization_code,refresh_token','http://localhost:8000/login/oauth2/code/messaging-client-oidc','http://localhost:8000/login/oauth2/code/messaging-client-oidc','read,write','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.reuse-refresh-tokens\":true,\"settings.token.id-token-signature-algorithm\":\"RS256\",\"settings.token.access-token-time-to-live\":300.0,\"settings.token.access-token-format\":{\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":3600.0}','N','2024-02-20 00:11:25.000','2024-02-20 00:11:25.000','system','system'),('base-gateway-local','base-gateway-local','2026-07-18 00:00:00','$2y$10$zjDbXviQ/DtuZ1d25DDDVux00UrjLmCGRNKL3kuEFmVxixfEpn7a.','2030-12-20 02:11:21','本地网关','client_secret_basic','client_credentials,authorization_code,refresh_token','http://localhost:3000/login/oauth2/code/base-gateway-local','http://localhost:3000/login/oauth2/code/base-gateway-local','read,openid,profile','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.access-token-format\": {\"value\": \"self-contained\"}, \"settings.token.reuse-refresh-tokens\": true, \"settings.token.access-token-time-to-live\": 7200.0, \"settings.token.refresh-token-time-to-live\": 2592000, \"settings.token.id-token-signature-algorithm\": \"RS256\"}','N','2026-07-18 00:00:00.000','2026-09-04 05:00:23.000','system','migration'),('iqc-platform','iqc-platform','2026-09-04 05:00:23','$2y$10$zjDbXviQ/DtuZ1d25DDDVux00UrjLmCGRNKL3kuEFmVxixfEpn7a.','2030-12-20 02:11:21','IQC 线上前端','client_secret_basic','client_credentials,authorization_code,refresh_token','http://opensabre:3010/login/oauth2/code/iqc-platform','http://opensabre:3010/login/oauth2/code/iqc-platform','read,openid,profile','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.access-token-format\": {\"value\": \"self-contained\"}, \"settings.token.reuse-refresh-tokens\": true, \"settings.token.access-token-time-to-live\": 7200.0, \"settings.token.refresh-token-time-to-live\": 2592000, \"settings.token.id-token-signature-algorithm\": \"RS256\"}','N','2026-09-04 05:00:22.743','2026-09-04 05:00:22.786','system','system'),('iqc-platform-local','iqc-platform-local','2026-09-04 05:00:23','$2y$10$zjDbXviQ/DtuZ1d25DDDVux00UrjLmCGRNKL3kuEFmVxixfEpn7a.','2030-12-20 02:11:21','IQC 本地前端','client_secret_basic','client_credentials,authorization_code,refresh_token','http://localhost:3010/login/oauth2/code/iqc-platform-local','http://localhost:3010/login/oauth2/code/iqc-platform-local','read,openid,profile','{\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}','{\"settings.token.access-token-format\": {\"value\": \"self-contained\"}, \"settings.token.reuse-refresh-tokens\": true, \"settings.token.access-token-time-to-live\": 7200.0, \"settings.token.refresh-token-time-to-live\": 2592000, \"settings.token.id-token-signature-algorithm\": \"RS256\"}','N','2026-09-04 05:00:22.696','2026-09-04 05:00:22.787','system','system');
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
