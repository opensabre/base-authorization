package io.github.opensabre.authorization.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationConsentQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationConsentVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 查询 Spring Authorization Server 的客户端授权同意表。
 */
@Repository
public class OAuth2AuthorizationConsentRepository {

    private static final String SELECT_COLUMNS = """
            SELECT consent.registered_client_id, client.client_id, client.client_name,
                   consent.principal_name, consent.authorities
              FROM oauth2_authorization_consent consent
              LEFT JOIN oauth2_registered_client client
                ON client.id = consent.registered_client_id
            """;

    private final JdbcTemplate jdbcTemplate;

    public OAuth2AuthorizationConsentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按客户端、主体和已授予权限分页查询授权同意记录。
     */
    public IPage<OAuth2AuthorizationConsentVo> query(
            Page<?> page, OAuth2AuthorizationConsentQueryParam param) {
        SqlCondition condition = buildCondition(param);
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM oauth2_authorization_consent consent "
                        + "LEFT JOIN oauth2_registered_client client "
                        + "ON client.id = consent.registered_client_id "
                        + condition.whereClause(),
                Long.class,
                condition.arguments().toArray());

        List<Object> arguments = new ArrayList<>(condition.arguments());
        arguments.add(page.getSize());
        arguments.add(page.offset());
        List<OAuth2AuthorizationConsentVo> records = jdbcTemplate.query(
                SELECT_COLUMNS + condition.whereClause()
                        + " ORDER BY client.client_id, consent.principal_name LIMIT ? OFFSET ?",
                rowMapper(),
                arguments.toArray());

        Page<OAuth2AuthorizationConsentVo> result =
                new Page<>(page.getCurrent(), page.getSize(), total == null ? 0 : total);
        result.setRecords(records);
        return result;
    }

    /**
     * 按 Spring Authorization Server 使用的复合主键查询授权同意记录。
     */
    public Optional<OAuth2AuthorizationConsentVo> findById(
            String registeredClientId, String principalName) {
        List<OAuth2AuthorizationConsentVo> records = jdbcTemplate.query(
                SELECT_COLUMNS
                        + " WHERE consent.registered_client_id = ? AND consent.principal_name = ?",
                rowMapper(),
                registeredClientId,
                principalName);
        return records.stream().findFirst();
    }

    private SqlCondition buildCondition(OAuth2AuthorizationConsentQueryParam param) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> arguments = new ArrayList<>();
        if (StringUtils.isNotBlank(param.getClientId())) {
            where.append(" AND client.client_id LIKE ?");
            arguments.add("%" + param.getClientId().trim() + "%");
        }
        if (StringUtils.isNotBlank(param.getPrincipalName())) {
            where.append(" AND consent.principal_name LIKE ?");
            arguments.add("%" + param.getPrincipalName().trim() + "%");
        }
        if (StringUtils.isNotBlank(param.getAuthority())) {
            // authorities 由 Spring Authorization Server 以逗号分隔存储，按完整项匹配。
            where.append(" AND CONCAT(',', consent.authorities, ',') LIKE ?");
            arguments.add("%," + param.getAuthority().trim() + ",%");
        }
        return new SqlCondition(where.toString(), arguments);
    }

    private RowMapper<OAuth2AuthorizationConsentVo> rowMapper() {
        return (resultSet, rowNum) -> {
            OAuth2AuthorizationConsentVo consent = new OAuth2AuthorizationConsentVo();
            consent.setRegisteredClientId(resultSet.getString("registered_client_id"));
            consent.setClientId(resultSet.getString("client_id"));
            consent.setClientName(resultSet.getString("client_name"));
            consent.setPrincipalName(resultSet.getString("principal_name"));
            consent.setAuthorities(resultSet.getString("authorities"));
            return consent;
        };
    }

    private record SqlCondition(String whereClause, List<Object> arguments) {
    }
}
