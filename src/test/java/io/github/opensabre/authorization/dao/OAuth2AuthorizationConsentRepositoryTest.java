package io.github.opensabre.authorization.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationConsentQueryParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2AuthorizationConsentRepositoryTest {

    private OAuth2AuthorizationConsentRepository repository;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource =
                new DriverManagerDataSource(
                        "jdbc:h2:mem:oauth2-consents;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP ALL OBJECTS");
        jdbcTemplate.execute("""
                CREATE TABLE oauth2_registered_client (
                    id VARCHAR(100) PRIMARY KEY,
                    client_id VARCHAR(100),
                    client_name VARCHAR(200)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE oauth2_authorization_consent (
                    registered_client_id VARCHAR(100),
                    principal_name VARCHAR(200),
                    authorities VARCHAR(1000),
                    PRIMARY KEY (registered_client_id, principal_name)
                )
                """);
        jdbcTemplate.update(
                "INSERT INTO oauth2_registered_client (id, client_id, client_name) VALUES (?, ?, ?)",
                "registered-client", "gateway-client", "Gateway");
        jdbcTemplate.update("""
                        INSERT INTO oauth2_authorization_consent
                            (registered_client_id, principal_name, authorities)
                        VALUES (?, ?, ?)
                        """,
                "registered-client", "alice", "SCOPE_openid,SCOPE_profile");
        repository = new OAuth2AuthorizationConsentRepository(jdbcTemplate);
    }

    @Test
    void shouldQueryConsentByClientPrincipalAndExactAuthority() {
        OAuth2AuthorizationConsentQueryParam param =
                new OAuth2AuthorizationConsentQueryParam();
        param.setClientId("gateway");
        param.setPrincipalName("ali");
        param.setAuthority("SCOPE_openid");

        var records = repository.query(new Page<>(1, 10), param).getRecords();

        assertThat(records).singleElement().satisfies(record -> {
            assertThat(record.getRegisteredClientId()).isEqualTo("registered-client");
            assertThat(record.getClientName()).isEqualTo("Gateway");
            assertThat(record.getAuthorities()).isEqualTo("SCOPE_openid,SCOPE_profile");
        });
    }

    @Test
    void shouldNotMatchPartialAuthorityName() {
        OAuth2AuthorizationConsentQueryParam param =
                new OAuth2AuthorizationConsentQueryParam();
        param.setAuthority("openid");

        assertThat(repository.query(new Page<>(1, 10), param).getRecords()).isEmpty();
    }
}
