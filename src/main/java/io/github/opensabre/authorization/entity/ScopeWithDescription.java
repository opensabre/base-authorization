package io.github.opensabre.authorization.entity;

import lombok.Data;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.HashMap;
import java.util.Map;

@Data
public class ScopeWithDescription {
    private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
    private static final Map<String, String> scopeDescriptions = new HashMap<>();

    public final String scope;
    public final String description;

    public ScopeWithDescription(String scope) {
        this.scope = scope;
        this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
    }

    static {
        scopeDescriptions.put(
                OidcScopes.PROFILE,
                "This application will be able to read your profile information."
        );
        scopeDescriptions.put(
                "message.read",
                "This application will be able to read your message."
        );
        scopeDescriptions.put(
                "message.write",
                "This application will be able to add new messages. It will also be able to edit and delete existing messages."
        );
        scopeDescriptions.put(
                "other.scope",
                "This is another scope example of a scope description."
        );
    }

}
