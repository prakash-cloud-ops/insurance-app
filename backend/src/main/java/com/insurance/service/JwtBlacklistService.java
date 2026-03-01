package com.insurance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(JwtBlacklistService.class);
    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public JwtBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Add a JWT token to the blacklist with expiry time
     * @param token The JWT token to blacklist
     * @param expiryMs Remaining token lifetime in milliseconds
     */
    public void blacklistToken(String token, long expiryMs) {
        try {
            String key = BLACKLIST_KEY_PREFIX + token;
            redisTemplate.opsForValue().set(key, "blacklisted", expiryMs, TimeUnit.MILLISECONDS);
            log.info("Token blacklisted successfully with TTL: {} ms", expiryMs);
        } catch (Exception e) {
            log.error("Failed to blacklist token in Redis: {}. Token may remain valid until expiry.", e.getMessage());
        }
    }

    /**
     * Check if a token is blacklisted
     * @param token The JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_KEY_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Failed to check token blacklist status in Redis: {}. Allowing token as fallback.", e.getMessage());
            return false;
        }
    }
}
