package io.github.opensabre.authorization.online;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OnlineUserServiceTest {

    private final FakeOnlineUserRedisOperations redisOperations = new FakeOnlineUserRedisOperations();
    private OnlineUserService service;

    @BeforeEach
    void setUp() {
        redisOperations.clear();
        service = new OnlineUserService(redisOperations, "opensabre:gateway:session");
    }

    @Test
    void shouldListOnlineUsersFromIndexAndRemoveStaleSessionIds() {
        redisOperations.setMembers("opensabre:gateway:session:online:sessions", Set.of("s1", "stale"));
        redisOperations.setEntries("opensabre:gateway:session:online:session:s1", Map.of(
                "username", "admin",
                "displayName", "管理员",
                "ip", "127.0.0.1",
                "userAgent", "Chrome",
                "authenticationType", "OAuth2AuthenticationToken",
                "loginTime", "2026-07-09T10:00:00",
                "lastAccessTime", "2026-07-09T10:10:00"
        ));
        redisOperations.setEntries("opensabre:gateway:session:online:session:stale", Map.of());

        var users = service.list(null);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getSessionId()).isEqualTo("s1");
        assertThat(users.get(0).getUsername()).isEqualTo("admin");
        assertThat(redisOperations.removed).contains("opensabre:gateway:session:online:sessions:stale");
    }

    @Test
    void shouldFilterOnlineUsersByUsername() {
        redisOperations.setMembers("opensabre:gateway:session:online:sessions", Set.of("s1"));
        redisOperations.setEntries("opensabre:gateway:session:online:session:s1", Map.of(
                "username", "admin",
                "lastAccessTime", "2026-07-09T10:10:00"
        ));

        assertThat(service.list("adm")).hasSize(1);
        assertThat(service.list("guest")).isEmpty();
    }

    @Test
    void shouldDeleteOnlineIndexAndSpringSessionWhenKickout() {
        service.kickout("s1");

        assertThat(redisOperations.removed).contains("opensabre:gateway:session:online:sessions:s1");
        assertThat(redisOperations.deleted).containsExactly(
                "opensabre:gateway:session:online:session:s1",
                "opensabre:gateway:session:sessions:s1",
                "opensabre:gateway:session:sessions:expires:s1"
        );
    }

    private static class FakeOnlineUserRedisOperations implements OnlineUserRedisOperations {
        private final Map<String, Set<String>> members = new HashMap<>();
        private final Map<String, Map<Object, Object>> entries = new HashMap<>();
        private final List<String> removed = new ArrayList<>();
        private final List<String> deleted = new ArrayList<>();

        void clear() {
            members.clear();
            entries.clear();
            removed.clear();
            deleted.clear();
        }

        void setMembers(String key, Set<String> values) {
            members.put(key, values);
        }

        void setEntries(String key, Map<Object, Object> values) {
            entries.put(key, values);
        }

        @Override
        public Set<String> members(String key) {
            return members.get(key);
        }

        @Override
        public Map<Object, Object> entries(String key) {
            return entries.get(key);
        }

        @Override
        public void remove(String key, String value) {
            removed.add(key + ":" + value);
        }

        @Override
        public void delete(String key) {
            deleted.add(key);
        }
    }
}
