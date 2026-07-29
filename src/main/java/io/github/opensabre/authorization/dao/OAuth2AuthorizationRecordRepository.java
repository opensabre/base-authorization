package io.github.opensabre.authorization.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationRecordVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class OAuth2AuthorizationRecordRepository {

    private static final String SELECT_COLUMNS = """
            SELECT a.id, c.client_id, c.client_name, a.principal_name,
                   a.authorization_grant_type, a.authorized_scopes,
                   a.access_token_type, a.authorization_code_expires_at,
                   a.access_token_issued_at,
                   a.access_token_expires_at, a.refresh_token_issued_at,
                   a.refresh_token_expires_at, a.oidc_id_token_expires_at,
                   a.user_code_expires_at, a.device_code_expires_at,
                   CASE WHEN a.oidc_id_token_value IS NULL THEN 0 ELSE 1 END AS has_id_token,
                   CASE WHEN a.device_code_value IS NULL THEN 0 ELSE 1 END AS has_device_code
              FROM oauth2_authorization a
              LEFT JOIN oauth2_registered_client c ON c.id = a.registered_client_id
            """;

    private final JdbcTemplate jdbcTemplate;

    public OAuth2AuthorizationRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public IPage<OAuth2AuthorizationRecordVo> query(Page<?> page, OAuth2AuthorizationQueryParam param) {
        SqlCondition condition = buildCondition(param);
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM oauth2_authorization a "
                        + "LEFT JOIN oauth2_registered_client c ON c.id = a.registered_client_id "
                        + condition.whereClause(),
                Long.class,
                condition.arguments().toArray());

        List<Object> arguments = new ArrayList<>(condition.arguments());
        arguments.add(page.getSize());
        arguments.add(page.offset());
        List<OAuth2AuthorizationRecordVo> records = jdbcTemplate.query(
                SELECT_COLUMNS + condition.whereClause()
                        + " ORDER BY a.access_token_issued_at DESC, a.id DESC LIMIT ? OFFSET ?",
                rowMapper(),
                arguments.toArray());

        Page<OAuth2AuthorizationRecordVo> result =
                new Page<>(page.getCurrent(), page.getSize(), total == null ? 0 : total);
        result.setRecords(records);
        return result;
    }

    public Optional<OAuth2AuthorizationRecordVo> findById(String id) {
        List<OAuth2AuthorizationRecordVo> records = jdbcTemplate.query(
                SELECT_COLUMNS + " WHERE a.id = ?",
                rowMapper(),
                id);
        return records.stream().findFirst();
    }

    /**
     * 删除至少签发过一种授权材料，且所有授权材料都已到期的聚合记录。
     * 空有效期不视为过期，防止误删尚未签发Token的授权码或设备码流程。
     *
     * @param expiredBefore 过期截止时间
     * @return 删除记录数
     */
    public int deleteExpiredBatch(Instant expiredBefore, int batchSize) {
        Timestamp cutoff = Timestamp.from(expiredBefore);
        List<String> ids = jdbcTemplate.queryForList("""
                SELECT id FROM oauth2_authorization
                 WHERE """ + expiredPredicate("?") + """
                 ORDER BY id
                 LIMIT ?
                """, String.class, cutoff, cutoff, cutoff, cutoff, cutoff, cutoff, batchSize);
        if (ids.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        List<Object> deleteArguments = new ArrayList<>(ids);
        deleteArguments.addAll(java.util.Collections.nCopies(6, cutoff));
        return jdbcTemplate.update(
                "DELETE FROM oauth2_authorization WHERE id IN (" + placeholders + ") AND "
                        + expiredPredicate("?"),
                deleteArguments.toArray());
    }

    private SqlCondition buildCondition(OAuth2AuthorizationQueryParam param) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> arguments = new ArrayList<>();
        if (StringUtils.isNotBlank(param.getClientId())) {
            where.append(" AND c.client_id LIKE ?");
            arguments.add("%" + param.getClientId().trim() + "%");
        }
        if (StringUtils.isNotBlank(param.getPrincipalName())) {
            where.append(" AND a.principal_name LIKE ?");
            arguments.add("%" + param.getPrincipalName().trim() + "%");
        }
        if (StringUtils.isNotBlank(param.getAuthorizationGrantType())) {
            where.append(" AND a.authorization_grant_type = ?");
            arguments.add(param.getAuthorizationGrantType().trim());
        }
        if ("ACTIVE".equalsIgnoreCase(param.getStatus())) {
            where.append(" AND (a.access_token_expires_at > CURRENT_TIMESTAMP")
                    .append(" OR a.oidc_id_token_expires_at > CURRENT_TIMESTAMP)");
        } else if ("REFRESHABLE".equalsIgnoreCase(param.getStatus())) {
            where.append(" AND (a.access_token_expires_at IS NULL OR a.access_token_expires_at <= CURRENT_TIMESTAMP)")
                    .append(" AND (a.oidc_id_token_expires_at IS NULL OR a.oidc_id_token_expires_at <= CURRENT_TIMESTAMP)")
                    .append(" AND a.refresh_token_expires_at > CURRENT_TIMESTAMP");
        } else if ("AUTHORIZING".equalsIgnoreCase(param.getStatus())) {
            where.append(" AND (a.access_token_expires_at IS NULL OR a.access_token_expires_at <= CURRENT_TIMESTAMP)")
                    .append(" AND (a.oidc_id_token_expires_at IS NULL OR a.oidc_id_token_expires_at <= CURRENT_TIMESTAMP)")
                    .append(" AND (a.refresh_token_expires_at IS NULL OR a.refresh_token_expires_at <= CURRENT_TIMESTAMP)")
                    .append(" AND (a.authorization_code_expires_at > CURRENT_TIMESTAMP")
                    .append(" OR a.user_code_expires_at > CURRENT_TIMESTAMP")
                    .append(" OR a.device_code_expires_at > CURRENT_TIMESTAMP)");
        } else if ("EXPIRED".equalsIgnoreCase(param.getStatus())) {
            where.append(" AND ").append(expiredPredicate("CURRENT_TIMESTAMP"));
        }
        return new SqlCondition(where.toString(), arguments);
    }

    private RowMapper<OAuth2AuthorizationRecordVo> rowMapper() {
        return (resultSet, rowNum) -> {
            OAuth2AuthorizationRecordVo record = new OAuth2AuthorizationRecordVo();
            record.setId(resultSet.getString("id"));
            record.setClientId(resultSet.getString("client_id"));
            record.setClientName(resultSet.getString("client_name"));
            record.setPrincipalName(resultSet.getString("principal_name"));
            record.setAuthorizationGrantType(resultSet.getString("authorization_grant_type"));
            record.setAuthorizedScopes(resultSet.getString("authorized_scopes"));
            record.setAccessTokenType(resultSet.getString("access_token_type"));
            record.setAuthorizationCodeExpiresAt(toDate(resultSet.getTimestamp("authorization_code_expires_at")));
            record.setAccessTokenIssuedAt(toDate(resultSet.getTimestamp("access_token_issued_at")));
            record.setAccessTokenExpiresAt(toDate(resultSet.getTimestamp("access_token_expires_at")));
            record.setIdTokenExpiresAt(toDate(resultSet.getTimestamp("oidc_id_token_expires_at")));
            record.setRefreshTokenIssuedAt(toDate(resultSet.getTimestamp("refresh_token_issued_at")));
            record.setRefreshTokenExpiresAt(toDate(resultSet.getTimestamp("refresh_token_expires_at")));
            record.setUserCodeExpiresAt(toDate(resultSet.getTimestamp("user_code_expires_at")));
            record.setDeviceCodeExpiresAt(toDate(resultSet.getTimestamp("device_code_expires_at")));
            record.setHasIdToken(resultSet.getBoolean("has_id_token"));
            record.setHasDeviceCode(resultSet.getBoolean("has_device_code"));
            record.setStatus(resolveStatus(record));
            return record;
        };
    }

    private static Date toDate(Timestamp timestamp) {
        return timestamp == null ? null : Date.from(timestamp.toInstant());
    }

    private static String resolveStatus(OAuth2AuthorizationRecordVo record) {
        Instant now = Instant.now();
        if (isAfter(record.getAccessTokenExpiresAt(), now) || isAfter(record.getIdTokenExpiresAt(), now)) {
            return "ACTIVE";
        }
        if (isAfter(record.getRefreshTokenExpiresAt(), now)) {
            return "REFRESHABLE";
        }
        if (isAfter(record.getAuthorizationCodeExpiresAt(), now)
                || isAfter(record.getUserCodeExpiresAt(), now)
                || isAfter(record.getDeviceCodeExpiresAt(), now)) {
            return "AUTHORIZING";
        }
        return "EXPIRED";
    }

    private static boolean isAfter(Date value, Instant instant) {
        return value != null && value.toInstant().isAfter(instant);
    }

    private static String expiredPredicate(String cutoff) {
        return """
                (authorization_code_expires_at IS NOT NULL
                    OR access_token_expires_at IS NOT NULL
                    OR oidc_id_token_expires_at IS NOT NULL
                    OR refresh_token_expires_at IS NOT NULL
                    OR user_code_expires_at IS NOT NULL
                    OR device_code_expires_at IS NOT NULL)
                AND (authorization_code_expires_at IS NULL OR authorization_code_expires_at <= %1$s)
                AND (access_token_expires_at IS NULL OR access_token_expires_at <= %1$s)
                AND (oidc_id_token_expires_at IS NULL OR oidc_id_token_expires_at <= %1$s)
                AND (refresh_token_expires_at IS NULL OR refresh_token_expires_at <= %1$s)
                AND (user_code_expires_at IS NULL OR user_code_expires_at <= %1$s)
                AND (device_code_expires_at IS NULL OR device_code_expires_at <= %1$s)
                """.formatted(cutoff);
    }

    private record SqlCondition(String whereClause, List<Object> arguments) {
    }
}
