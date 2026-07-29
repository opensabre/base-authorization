# 授权服务错误码注册

`ErrorCatalogConfiguration` 通过 `ErrorCatalogProvider.of("authorization", AuthErrorType.values())` 声明授权服务业务错误码。应用就绪后由 Governance Starter 异步注册到 Sysadmin。

注册需要配置与 Sysadmin 相同的 `ERROR_CATALOG_REGISTRATION_TOKEN`。失败不阻塞授权服务启动，但会导致管理端目录缺项，应监控启动告警。

错误码发布后保持语义稳定；废弃错误码不要立即复用。
