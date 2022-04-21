package com.example.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonClientConfig {

    @Bean
    public RedissonClient allocationRedissonClient(
            @Qualifier("allocationRedissonConfig") Config allocationRedissonConfig
    ) {
        return Redisson.create(allocationRedissonConfig);
    }

    @Bean
    protected Config allocationRedissonConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(0);
        return config;
    }
}
