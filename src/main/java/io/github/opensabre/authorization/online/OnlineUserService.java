package io.github.opensabre.authorization.online;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class OnlineUserService {

    private final OnlineUserRedisOperations redisOperations;
    private final String sessionNamespace;

    public OnlineUserService(OnlineUserRedisOperations redisOperations,
                             @Value("${opensabre.online-user.session-namespace:opensabre:gateway:session}") String sessionNamespace) {
        this.redisOperations = redisOperations;
        this.sessionNamespace = sessionNamespace;
    }

    public List<OnlineUser> list(String username) {
        Set<String> sessionIds = redisOperations.members(OnlineUserKeys.onlineSessionsKey(sessionNamespace));
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }
        return sessionIds.stream()
                .map(this::readUser)
                .flatMap(Optional::stream)
                .filter(user -> !StringUtils.hasText(username) || contains(user.getUsername(), username))
                .sorted(Comparator.comparing(OnlineUser::getLastAccessTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public Boolean kickout(String sessionId) {
        redisOperations.remove(OnlineUserKeys.onlineSessionsKey(sessionNamespace), sessionId);
        redisOperations.delete(OnlineUserKeys.onlineSessionKey(sessionNamespace, sessionId));
        redisOperations.delete(OnlineUserKeys.springSessionKey(sessionNamespace, sessionId));
        redisOperations.delete(OnlineUserKeys.springSessionExpiresKey(sessionNamespace, sessionId));
        return Boolean.TRUE;
    }

    private Optional<OnlineUser> readUser(String sessionId) {
        String onlineSessionKey = OnlineUserKeys.onlineSessionKey(sessionNamespace, sessionId);
        Map<Object, Object> entries = redisOperations.entries(onlineSessionKey);
        if (entries == null || entries.isEmpty()) {
            redisOperations.remove(OnlineUserKeys.onlineSessionsKey(sessionNamespace), sessionId);
            return Optional.empty();
        }
        return Optional.of(OnlineUser.builder()
                .sessionId(sessionId)
                .username(value(entries, "username"))
                .displayName(value(entries, "displayName"))
                .ip(value(entries, "ip"))
                .userAgent(value(entries, "userAgent"))
                .authenticationType(value(entries, "authenticationType"))
                .loginTime(value(entries, "loginTime"))
                .lastAccessTime(value(entries, "lastAccessTime"))
                .build());
    }

    private static boolean contains(String source, String keyword) {
        return StringUtils.hasText(source) && source.contains(keyword);
    }

    private static String value(Map<Object, Object> entries, String key) {
        return Objects.toString(entries.get(key), null);
    }
}
