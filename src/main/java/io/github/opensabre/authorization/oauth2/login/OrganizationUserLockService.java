package io.github.opensabre.authorization.oauth2.login;

import io.github.opensabre.authorization.entity.User;
import io.github.opensabre.authorization.entity.form.OrganizationUserForm;
import io.github.opensabre.authorization.provider.OrganizationProvider;
import io.github.opensabre.authorization.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationUserLockService implements UserLockService {

    private final IUserService userService;
    private final OrganizationProvider organizationProvider;

    @Override
    public void lock(String username) {
        User user = userService.getByUniqueId(username);
        if (user == null || user.getId() == null) {
            log.warn("Skip locking user because user not found: {}", username);
            return;
        }

        OrganizationUserForm userForm = new OrganizationUserForm();
        userForm.setUsername(user.getUsername());
        userForm.setMobile(user.getMobile());
        userForm.setName(user.getName());
        userForm.setEnabled(user.getEnabled());
        userForm.setAccountNonExpired(user.getAccountNonExpired());
        userForm.setCredentialsNonExpired(user.getCredentialsNonExpired());
        userForm.setAccountNonLocked(false);

        boolean updated = organizationProvider.updateUser(user.getId(), userForm);
        if (!updated) {
            log.warn("Failed to lock user: username={}, userId={}", username, user.getId());
            return;
        }
        userService.invalidateByUniqueId(username);
    }
}
