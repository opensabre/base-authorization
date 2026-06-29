# Repository Guidelines

## Project Structure & Module Organization

This is a Java 17 Maven service for an OAuth2 authorization server. Main code lives in `src/main/java/io/github/opensabre/authorization`, organized by responsibility: `config`, `oauth2`, `service`, `dao`, `entity`, `provider`, `rest`, and `exception`. Runtime configuration is in `src/main/resources/application.yml` and `bootstrap.yml`. SQL initialization scripts are in `src/main/resources/db`. Thymeleaf pages are in `src/main/resources/templates`, and static CSS/images are in `src/main/resources/static/assets`. Tests mirror the main package under `src/test/java`; test configuration is in `src/test/resources/application.yml`.

## Build, Test, and Development Commands

- `mvn test`: runs JUnit 5/Spring Boot tests, using H2 where configured for test database behavior.
- `mvn package`: compiles, tests, and builds the executable JAR under `target/`.
- `mvn spring-boot:run`: starts the service locally. The README expects the app at `http://localhost:8000`.
- `mvn jib:build`: builds and pushes the container image via the configured Jib Maven plugin.
- `mvn -Pnative package`: runs the native profile when GraalVM/native tooling is available.

Before local startup, provision MySQL, RabbitMQ, and Nacos, then apply the DDL/DML scripts from `src/main/resources/db`.

## Coding Style & Naming Conventions

Use Java 17 and the existing Spring Boot style. Keep package names under `io.github.opensabre.authorization`. Use 4-space indentation, constructor or resource injection consistently with surrounding classes, and clear suffixes such as `Controller`, `Service`, `ServiceImpl`, `Mapper`, `Config`, `Provider`, `Po`, `Vo`, `Form`, and `Param`. Prefer descriptive method names and keep OAuth2/security configuration changes localized to `config` or `oauth2`.

## Testing Guidelines

Tests use JUnit 5 with `spring-boot-starter-test`; integration-style tests may use `@SpringBootTest`. Name test classes with the `*Test` suffix and place them in the matching package under `src/test/java`. For persistence or conversion logic, add focused tests near existing examples such as `RegisteredClientMapperTest` and `RegisteredClientConvertTest`. Run `mvn test` before submitting changes.

## Commit & Pull Request Guidelines

Recent history uses short Chinese commit subjects and occasional Conventional Commit prefixes, for example `fix: 修复token有效期问题` and `chore: 添加swagger文档标题`. Prefer `fix:`, `chore:`, `feat:`, or `test:` plus a concise summary. Pull requests should describe the behavior changed, list verification commands, note configuration or database script changes, and link related issues. Include screenshots only for template or static asset UI changes.

## Security & Configuration Tips

Do not commit real client secrets, database passwords, registry credentials, or private keys. Keep environment-specific values in external configuration or deployment secrets, and treat the README sample credentials as local/demo data only.
