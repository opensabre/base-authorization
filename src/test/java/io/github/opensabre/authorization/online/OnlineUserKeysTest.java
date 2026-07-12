package io.github.opensabre.authorization.online;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OnlineUserKeysTest {

    @Test
    void shouldBuildOnlineUserAndSpringSessionKeysFromSessionNamespace() {
        assertThat(OnlineUserKeys.onlineSessionsKey("opensabre:gateway:session"))
                .isEqualTo("opensabre:gateway:session:online:sessions");
        assertThat(OnlineUserKeys.onlineSessionKey("opensabre:gateway:session", "s1"))
                .isEqualTo("opensabre:gateway:session:online:session:s1");
        assertThat(OnlineUserKeys.springSessionKey("opensabre:gateway:session", "s1"))
                .isEqualTo("opensabre:gateway:session:sessions:s1");
        assertThat(OnlineUserKeys.springSessionExpiresKey("opensabre:gateway:session", "s1"))
                .isEqualTo("opensabre:gateway:session:sessions:expires:s1");
    }
}
