package io.github.opensabre.authorization.oauth2;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Component
public class JWKCacheManager {

    private static final String AUTHORIZATION_JWS_PREFIX_KEY = "authorization_jws";

    @Value("${spring.application.name}")
    private String application;

    @Resource
    private CacheManager cacheManager;
    private Cache<String, JWKSet> jwkSetCache;

    @PostConstruct
    public void init() {
        QuickConfig qc = QuickConfig.newBuilder("remote_")
                .expire(Duration.ofDays(36500L)) // 100年过期时间
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        jwkSetCache = cacheManager.getOrCreateCache(qc);
    }

    /**
     * 缓存JWK密钥
     *
     * @return 返回JWKSet
     */
    public JWKSet getJWKSet() {
        return jwkSetCache.computeIfAbsent(getJWKKey(), key -> {
            // 生成密钥对
            KeyPair keyPair = generateRsaKey();
            RSAKey rsaKey = getRsaKey(keyPair);
            // 生成jws
            return new JWKSet(rsaKey);
        });
    }

    /**
     * cache key
     *
     * @return cache key
     */
    private String getJWKKey() {
        return AUTHORIZATION_JWS_PREFIX_KEY + ":" + application;
    }

    /**
     * 将keyPair转换为RSAKey
     *
     * @param keyPair 密钥对
     * @return 返回RSAKey
     */
    private static RSAKey getRsaKey(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    /**
     * 生成密钥对,启动时生成的带有密钥的实例java.security.KeyPair用于创建JWKSource上述内容
     *
     * @return KeyPair
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}