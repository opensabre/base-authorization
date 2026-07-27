package io.github.opensabre.authorization.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2AuthorizationRecordRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private OAuth2AuthorizationRecordRepository repository;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource =
                new DriverManagerDataSource("jdbc:h2:mem:oauth2-records;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP ALL OBJECTS");
        jdbcTemplate.execute("""
                CREATE TABLE oauth2_registered_client (
                    id VARCHAR(100) PRIMARY KEY,
                    client_id VARCHAR(100),
                    client_name VARCHAR(200)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE oauth2_authorization (
                    id VARCHAR(100) PRIMARY KEY,
                    registered_client_id VARCHAR(100),
                    principal_name VARCHAR(200),
                    authorization_grant_type VARCHAR(100),
                    authorized_scopes VARCHAR(1000),
                    access_token_type VARCHAR(100),
                    access_token_issued_at TIMESTAMP,
                    access_token_expires_at TIMESTAMP,
                    authorization_code_expires_at TIMESTAMP,
                    oidc_id_token_expires_at TIMESTAMP,
                    refresh_token_issued_at TIMESTAMP,
                    refresh_token_expires_at TIMESTAMP,
                    user_code_expires_at TIMESTAMP,
                    device_code_expires_at TIMESTAMP,
                    oidc_id_token_value BLOB,
                    device_code_value BLOB
                )
                """);
        jdbcTemplate.update(
                "INSERT INTO oauth2_registered_client (id, client_id, client_name) VALUES (?, ?, ?)",
                "client-record", "gateway-client", "Gateway");
        repository = new OAuth2AuthorizationRecordRepository(jdbcTemplate);
    }

    @Test
    void shouldKeepActiveAndRefreshableStatusesDistinct() {
        Instant now = Instant.now();
        insert("active", now.minusSeconds(30), now.plusSeconds(60), now.plusSeconds(120));
        insert("refreshable", now.minusSeconds(30), now.minusSeconds(1), now.plusSeconds(120));
        insert("expired", now.minusSeconds(120), now.minusSeconds(60), now.minusSeconds(1));

        assertThat(query("ACTIVE")).containsExactly("active");
        assertThat(query("REFRESHABLE")).containsExactly("refreshable");
        assertThat(query("EXPIRED")).containsExactly("expired");
    }

    @Test
    void shouldDeleteOnlyRecordsWhoseEveryAuthorizationMaterialHasExpired() {
        Instant now = Instant.now();
        insert("expired", now.minusSeconds(120), now.minusSeconds(60), now.minusSeconds(1));
        insert("refreshable", now.minusSeconds(30), now.minusSeconds(1), now.plusSeconds(120));
        insert("active-device-code", now.minusSeconds(30), now.minusSeconds(1), now.minusSeconds(1));
        jdbcTemplate.update(
                "UPDATE oauth2_authorization SET device_code_expires_at = ? WHERE id = ?",
                Timestamp.from(now.plusSeconds(60)), "active-device-code");

        assertThat(repository.deleteExpired(now)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForList(
                "SELECT id FROM oauth2_authorization ORDER BY id", String.class))
                .containsExactly("active-device-code", "refreshable");
    }

    private java.util.List<String> query(String status) {
        OAuth2AuthorizationQueryParam param = new OAuth2AuthorizationQueryParam();
        param.setStatus(status);
        return repository.query(new Page<>(1, 20), param).getRecords().stream()
                .map(record -> record.getId())
                .toList();
    }

    private void insert(String id, Instant issuedAt, Instant accessExpiresAt, Instant refreshExpiresAt) {
        jdbcTemplate.update("""
                        INSERT INTO oauth2_authorization (
                            id, registered_client_id, principal_name, authorization_grant_type,
                            authorized_scopes, access_token_type, access_token_issued_at,
                            access_token_expires_at, refresh_token_issued_at, refresh_token_expires_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                id, "client-record", "operator", "authorization_code", "openid,profile", "Bearer",
                Timestamp.from(issuedAt), Timestamp.from(accessExpiresAt),
                Timestamp.from(issuedAt), Timestamp.from(refreshExpiresAt));
    }
}
