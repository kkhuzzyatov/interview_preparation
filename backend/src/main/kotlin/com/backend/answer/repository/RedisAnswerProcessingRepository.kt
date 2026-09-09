package com.backend.answer.repository

import com.backend.answer.entity.AnswerProcessing
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.UUID

@Repository
class RedisAnswerProcessingRepository(
    private val redisTemplate: RedisTemplate<String, AnswerProcessing>,
    private val stringRedisTemplate: StringRedisTemplate,
) : AnswerProcessingRepository {
    override fun save(processing: AnswerProcessing) {
        redisTemplate.opsForValue().set(
            processingKey(processing.answerId),
            processing,
            Duration.ofMinutes(30),
        )

        stringRedisTemplate.opsForValue().set(
            userKey(processing.userId),
            processing.answerId.toString(),
            Duration.ofMinutes(30),
        )
    }

    override fun findByAnswerId(answerId: UUID): AnswerProcessing? =
        redisTemplate
            .opsForValue()
            .get(processingKey(answerId))

    override fun findByUserId(userId: UUID): AnswerProcessing? {
        val answerId =
            stringRedisTemplate
                .opsForValue()
                .get(userKey(userId))
                ?: return null

        return findByAnswerId(UUID.fromString(answerId))
    }

    override fun deleteByAnswerId(answerId: UUID) {
        val processing = findByAnswerId(answerId)

        redisTemplate.delete(processingKey(answerId))

        if (processing != null) {
            stringRedisTemplate.delete(
                userKey(processing.userId),
            )
        }
    }

    private fun processingKey(answerId: UUID): String = "answer-processing:$answerId"

    private fun userKey(userId: UUID): String = "answer-processing:user:$userId"
}
