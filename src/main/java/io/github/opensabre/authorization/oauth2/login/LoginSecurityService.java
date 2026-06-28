package io.github.opensabre.authorization.oauth2.login;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginSecurityService {

    private final LoginFailureRepository loginFailureRepository;
    private final UserLockService userLockService;
    private final LoginSecurityProperties properties;

    public int recordPasswordFailure(String username) {
        if (StringUtils.isBlank(username)) {
            return 0;
        }
        int failureCount = loginFailureRepository.increment(username);
        if (failureCount >= properties.getLockThreshold()) {
            userLockService.lock(username);
        }
        return failureCount;
    }

    public boolean isCaptchaRequired(String username) {
        return getFailureCount(username) >= properties.getCaptchaThreshold();
    }

    public int getFailureCount(String username) {
        if (StringUtils.isBlank(username)) {
            return 0;
        }
        return loginFailureRepository.getCount(username);
    }

    public void resetFailures(String username) {
        if (StringUtils.isNotBlank(username)) {
            loginFailureRepository.reset(username);
        }
    }
}
