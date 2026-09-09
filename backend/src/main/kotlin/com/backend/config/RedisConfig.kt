package com.backend.config

import com.backend.answer.entity.AnswerProcessing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper

@Configuration
class RedisConfig {
    @Bean
    fun answerProcessingRedisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): RedisTemplate<String, AnswerProcessing> {
        val template = RedisTemplate<String, AnswerProcessing>()

        template.connectionFactory = connectionFactory

        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        val valueSerializer =
            JacksonJsonRedisSerializer(
                objectMapper,
                AnswerProcessing::class.java,
            )

        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer

        template.afterPropertiesSet()

        return template
    }
}
