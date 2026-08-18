# 开发与运行

## 启动前检查

1. 按 `src/main/resources/db/` 中 DDL、DML 初始化授权服务数据库。
2. 配置文件中的注册中心、数据库和缓存依赖应与当前环境一致。
3. 使用 `mvn spring-boot:run` 启动；镜像构建以项目 Maven 配置为准。

## 验证

- 使用授权端点完成一次客户端凭证或用户登录授权。
- 检查令牌声明、客户端校验和在线用户查询。
- 变更数据库结构时同时验证空库初始化与升级脚本（若已提供）。

## OAuth2 客户端审计时间迁移

`V20260818_01__use_millisecond_precision_for_client_audit_timestamps.sql` 将
`oauth2_registered_client` 的公共审计字段升级为 `DATETIME(3)`；`updated_time` 由
`ON UPDATE CURRENT_TIMESTAMP(3)` 自动维护。该变更不调整授权记录的签发、过期等协议时间字段，
以免改变 OAuth2 的过期与清理语义。

迁移前已被秒级 `DATETIME` 截断的毫秒无法恢复。发布前备份目标库并记录迁移版本；回滚为
`DATETIME` 会再次截断迁移后写入的毫秒。

## 变更要求

- 修改 OAuth2 声明、Scope、登录校验或锁定策略时，更新 `modules/oauth2.md`。
- 不在文档、示例或提交中写入真实账号、令牌和密钥。
