package com.github.llm_redis_stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamReceiver;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, String> serializationContext =
                RedisSerializationContext
                        .<String, String>newSerializationContext(new StringRedisSerializer())
                        .key(new StringRedisSerializer())
                        .value(new StringRedisSerializer())
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new StringRedisSerializer())
                        .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public StreamReceiver<String, MapRecord<String, String, String>> streamReceiver(
            ReactiveRedisConnectionFactory connectionFactory) {
        return StreamReceiver.create(connectionFactory);
    }

}
