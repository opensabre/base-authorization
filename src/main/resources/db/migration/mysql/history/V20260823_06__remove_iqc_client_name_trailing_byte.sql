-- 去除字符集修复脚本生成时误带入的末尾换行字节。
SET NAMES utf8mb4;

UPDATE oauth2_registered_client
SET client_name = CONVERT(X'49514320E7BABFE4B88AE5898DE7ABAF' USING utf8mb4),
    updated_time = NOW(3), updated_by = 'system'
WHERE client_id = 'iqc-platform';

UPDATE oauth2_registered_client
SET client_name = CONVERT(X'49514320E69CACE59CB0E5898DE7ABAF' USING utf8mb4),
    updated_time = NOW(3), updated_by = 'system'
WHERE client_id = 'iqc-platform-local';
