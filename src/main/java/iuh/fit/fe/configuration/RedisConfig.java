package iuh.fit.fe.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            // Check Connection
            connectionFactory.getConnection().ping();
            log.info("Redis is connected successfully!");
        } catch (Exception e) {
            log.error("Unable to connect to Redis: {}", e.getMessage());
        }
        return new StringRedisTemplate(connectionFactory);
    }
}
