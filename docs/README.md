# base-authorization 研发文档

本目录是授权服务的研发知识入口。根目录 `readme.md` 保留为仓库入口和历史使用说明；本目录维护面向研发的模块、流程和演进文档。

## 导航

| 文档 | 用途 |
| --- | --- |
| [架构与边界](architecture.md) | 服务职责、依赖和事实源 |
| [开发与运行](development.md) | 本地启动、配置和验证 |
| [模块：OAuth2 授权与登录](modules/oauth2.md) | 功能、使用、关键流程和规划 |
| [规划](roadmap.md) | 已知演进方向与待确认事项 |

## 维护约定

- 接口以 `src/main/java/**/rest` 中的 Controller 为准。
- 表结构与初始数据以 `src/main/resources/db/` 为准。
- 每次涉及授权流程、登录安全或令牌声明的改动，必须同步更新模块文档。
