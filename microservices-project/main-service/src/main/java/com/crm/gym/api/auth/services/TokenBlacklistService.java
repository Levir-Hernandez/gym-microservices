package com.crm.gym.api.auth.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class TokenBlacklistService
{
    private static final String PREFIX = "blacklist:token";

    private RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    public boolean isTokenBlacklisted(String jti)
    {
        return redisTemplate.hasKey(PREFIX + jti);
    }

    public void blacklistToken(String jti, long expirationMillis)
    {
        redisTemplate.opsForValue().set(PREFIX + jti, "1", Duration.ofMillis(expirationMillis));
    }

    public void blacklistTokenFromClaims(Claims claims)
    {
        long now = System.currentTimeMillis();
        blacklistToken(claims.getId(), claims.getExpiration().getTime() - now);
    }
}