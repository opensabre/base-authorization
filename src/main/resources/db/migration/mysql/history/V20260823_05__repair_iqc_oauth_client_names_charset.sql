-- 修复早期手工导入时连接字符集错误造成的 IQC 客户端名称双重编码。
SET NAMES utf8mb4;

UPDATE oauth2_registered_client
SET client_name = CONVERT(X'49514320E7BABFE4B88AE5898DE7ABAF0A' USING utf8mb4),
    updated_time = NOW(3),
    updated_by = 'system'
WHERE client_id = 'iqc-platform';

UPDATE oauth2_registered_client
SET client_name = CONVERT(X'49514320E69CACE59CB0E5898DE7ABAF0A' USING utf8mb4),
    updated_time = NOW(3),
    updated_by = 'system'
WHERE client_id = 'iqc-platform-local';
