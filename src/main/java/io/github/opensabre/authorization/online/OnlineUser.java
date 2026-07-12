package io.github.opensabre.authorization.online;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnlineUser {

    private String sessionId;
    private String username;
    private String displayName;
    private String ip;
    private String userAgent;
    private String authenticationType;
    private String loginTime;
    private String lastAccessTime;
}
