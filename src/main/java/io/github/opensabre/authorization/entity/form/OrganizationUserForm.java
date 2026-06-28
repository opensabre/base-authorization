package io.github.opensabre.authorization.entity.form;

import lombok.Data;

@Data
public class OrganizationUserForm {

    private String username;
    private String password;
    private String mobile;
    private String name;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Boolean accountNonLocked;
}
