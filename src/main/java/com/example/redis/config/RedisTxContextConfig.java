package com.example.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@EnableTransactionManagement
@Configuration
public class RedisTxContextConfig {

    @Bean
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
        // explicitly enable transaction support
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // jedis || Lettuce
        // todo redisson의 커넥션 팩토리 찾아보기
    }

    /**
     * 트랜잭션 관리에는 PlatformTransactionManager.PlatformTransactionManagerSpring Data Redis는 구현 과 함께 제공되지 않습니다.
     * 애플리케이션이 JDBC를 사용한다고 가정하면 Spring Data Redis는 기존 트랜잭션 관리자를 사용하여 트랜잭션에 참여할 수 있습니다.
     *
     * 즉, 다른 라이브러리의 트랜잭션 관리자 객체에 Redis 정보를 넣어줘야 함
     * -> 그래서 이 샘플에서는 JPA 가 필요 없지만, JPA 의존성 추가했음.
     */
    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        // ... todo
    }
}