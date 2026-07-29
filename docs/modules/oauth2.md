# OAuth2 授权与登录

## 模块介绍

本模块提供授权服务的 OAuth2 授权、登录、设备授权、客户端管理、验证码和在线用户能力。代码入口主要位于 `oauth2/`、`rest/` 与 `online/`。

## 功能

- 授权请求与令牌签发；
- 客户端注册和客户端凭证校验；
- 登录验证码与登录安全控制；
- 在线用户查询与管理；
- 设备授权相关流程。

## 使用

调用路径和请求字段以 `AuthorizationController`、`LoginCaptchaController`、`OnlineUserController`、`RegisteredClientController` 的映射及 OpenAPI 文档为准。根目录 `readme.md` 中的示例仅作为历史参考，实际客户端、Scope 与安全策略以运行配置和数据库数据为准。

## 关键流程

```text
客户端/用户 → 授权服务校验客户端与登录状态 → 签发令牌 → 网关/下游服务使用令牌
                         ↓
                    验证码、锁定与在线会话记录
```

## 规划

- 明确 OIDC、设备授权和客户端自助管理的支持范围；
- 将令牌声明、过期策略和注销语义沉淀为可测试的兼容性契约；
- 为高风险认证操作补齐审计与异常处置 Runbook。

## 授权记录管理 API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/authorizations/conditions` | 分页查询，支持客户端、主体、授权类型和状态条件 |
| `GET` | `/authorizations/{id}` | 查看授权记录详情 |
| `DELETE` | `/authorizations/{id}` | 删除服务端授权并阻止 Refresh Token 继续使用 |
| `DELETE` | `/authorizations/expired/cleanup` | 仅清理所有 Token、授权码和设备码均已过期的记录 |

终止服务端授权不能撤回已经签发的自包含 JWT Access Token；它仍有效到 `exp`。清理操作带审计，管理端按钮和后端资源权限分别由 Organization 服务配置。

授权记录状态按聚合中仍有效的授权材料统一计算，优先级为：

- `ACTIVE`：Access Token 或 ID Token 仍有效；
- `REFRESHABLE`：前述 Token 已失效，但 Refresh Token 仍有效；
- `AUTHORIZING`：尚未完成令牌签发，Authorization Code、User Code 或 Device Code 仍有效；
- `EXPIRED`：签发过授权材料且全部失效，此状态与清理资格一致。

## 过期记录保留与定时清理

定时清理默认关闭。启用后只删除“全部授权材料失效时间早于当前时间减保留期”的记录，并按批次和单次最大批数限流；中途失败时已完成批次保留，下次调度从剩余记录继续。

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `opensabre.oauth2.authorization.cleanup.enabled` | `false` | 是否启用定时清理 |
| `opensabre.oauth2.authorization.cleanup.cron` | `0 0 * * * *` | 每小时执行一次 |
| `opensabre.oauth2.authorization.cleanup.retention` | `7d` | 过期记录保留期 |
| `opensabre.oauth2.authorization.cleanup.batch-size` | `500` | 单批删除上限 |
| `opensabre.oauth2.authorization.cleanup.max-batches-per-run` | `20` | 单次任务最大批数 |

任务复用现有审计事件链路，执行结果会进入统一审计日志；同时暴露
`opensabre.oauth2.authorization.cleanup.runs`、`opensabre.oauth2.authorization.cleanup.deleted`
和 `opensabre.oauth2.authorization.cleanup.duration` 指标。
