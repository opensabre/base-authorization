-- Execute once in version order. DATETIME is timezone-neutral; previously truncated milliseconds cannot be recovered.
ALTER TABLE oauth2_registered_client
    MODIFY COLUMN created_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    MODIFY COLUMN updated_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间';
