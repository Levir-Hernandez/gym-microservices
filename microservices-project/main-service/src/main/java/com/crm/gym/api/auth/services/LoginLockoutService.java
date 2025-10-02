package com.crm.gym.api.auth.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Service
public class LoginLockoutService
{
    private static final String PREFIX = "lockout:login";

    private Integer maxAttempts;
    private Long durationMillis;

    private RedisTemplate<String, String> redisTemplate;

    public LoginLockoutService(
            @Value("${auth.login.lockout.max-attempts:3}") Integer maxAttempts,
            @Value("${auth.login.lockout.duration:300}") Long duration, // 5 minutes
            RedisTemplate<String, String> redisTemplate
    )
    {
        this.redisTemplate = redisTemplate;
        this.maxAttempts = maxAttempts;
        this.durationMillis = duration*1000;
    }

    public boolean isUserBlocked(String username)
    {
        String attempts = redisTemplate.opsForValue().get(PREFIX + username);

        return Optional.ofNullable(attempts)
                .map(Integer::parseInt)
                .map(n -> n >= maxAttempts)
                .orElse(false);
    }

    public void incrementUserFailedLogins(String username)
    {
        String key = PREFIX + username;
        Long attempts = redisTemplate.opsForValue().increment(key);

        if(Objects.nonNull(attempts) && attempts >= maxAttempts)
        {
            redisTemplate.expire(key, Duration.ofMillis(durationMillis));
        }
    }

    public void resetUserFailedLogins(String username)
    {
        redisTemplate.delete(PREFIX + username);
    }
}