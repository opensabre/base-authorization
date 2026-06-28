package io.github.opensabre.authorization.oauth2.login;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginSecurityServiceTest {

    private final InMemoryLoginFailureRepository failureRepository = new InMemoryLoginFailureRepository();
    private final RecordingUserLockService userLockService = new RecordingUserLockService();
    private final LoginSecurityService loginSecurityService = new LoginSecurityService(
            failureRepository,
            userLockService,
            new LoginSecurityProperties()
    );

    @Test
    void captchaIsRequiredAfterTwoPasswordFailures() {
        assertFalse(loginSecurityService.isCaptchaRequired("admin"));

        assertEquals(1, loginSecurityService.recordPasswordFailure("admin"));
        assertFalse(loginSecurityService.isCaptchaRequired("admin"));

        assertEquals(2, loginSecurityService.recordPasswordFailure("admin"));
        assertTrue(loginSecurityService.isCaptchaRequired("admin"));
    }

    @Test
    void successfulLoginClearsPasswordFailures() {
        loginSecurityService.recordPasswordFailure("admin");
        loginSecurityService.recordPasswordFailure("admin");

        loginSecurityService.resetFailures("admin");

        assertFalse(loginSecurityService.isCaptchaRequired("admin"));
        assertEquals(0, loginSecurityService.getFailureCount("admin"));
    }

    @Test
    void fifthPasswordFailureLocksUser() {
        for (int i = 0; i < 5; i++) {
            loginSecurityService.recordPasswordFailure("admin");
        }

        assertEquals("admin", userLockService.lockedUsername);
    }

    private static class InMemoryLoginFailureRepository implements LoginFailureRepository {

        private final Map<String, Integer> counts = new HashMap<>();

        @Override
        public int increment(String username) {
            int count = getCount(username) + 1;
            counts.put(username, count);
            return count;
        }

        @Override
        public int getCount(String username) {
            return counts.getOrDefault(username, 0);
        }

        @Override
        public void reset(String username) {
            counts.remove(username);
        }
    }

    private static class RecordingUserLockService implements UserLockService {

        private String lockedUsername;

        @Override
        public void lock(String username) {
            this.lockedUsername = username;
        }
    }
}
