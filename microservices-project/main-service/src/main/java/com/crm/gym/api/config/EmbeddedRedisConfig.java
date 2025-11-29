package com.crm.gym.api.config;

import java.io.IOException;

import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Profile("standalone")
public class EmbeddedRedisConfig
{
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RedisServer redisServer() throws IOException
    {
        return new RedisServer(6379);
    }
}