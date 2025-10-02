package com.crm.gym.api.config;

import java.io.IOException;
import redis.embedded.RedisServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// For local testing without docker-compose
@Configuration
public class EmbeddedRedisConfig
{
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RedisServer redisServer() throws IOException
    {
        return new RedisServer(6379);
    }
}