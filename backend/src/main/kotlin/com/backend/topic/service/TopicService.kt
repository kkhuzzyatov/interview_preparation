package com.backend.topic.service

import com.backend.topic.controller.dto.CreateTopicRequest
import com.backend.topic.controller.dto.TopicResponse
import com.backend.topic.controller.dto.UpdateTopicRequest
import com.backend.topic.entity.Topic
import com.backend.topic.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TopicService(
    private val topicRepository: TopicRepository,
) {
    @Transactional(readOnly = true)
    fun getAll(): List<TopicResponse> =
        topicRepository
            .findAll()
            .map(::toResponse)

    @Transactional(readOnly = true)
    fun getById(id: UUID): TopicResponse =
        topicRepository
            .findById(id)
            .map(::toResponse)
            .orElseThrow {
                NoSuchElementException("Topic with id $id not found")
            }

    @Transactional
    fun create(request: CreateTopicRequest): TopicResponse {
        val topic =
            Topic(
                id = UUID.randomUUID(),
                name = request.name.trim(),
            )

        return toResponse(topicRepository.save(topic))
    }

    @Transactional
    fun update(
        id: UUID,
        request: UpdateTopicRequest,
    ): TopicResponse {
        val topic =
            topicRepository
                .findById(id)
                .orElseThrow {
                    NoSuchElementException("Topic with id $id not found")
                }

        topic.name = request.name.trim()

        return toResponse(topicRepository.save(topic))
    }

    @Transactional
    fun delete(id: UUID) {
        val topic =
            topicRepository
                .findById(id)
                .orElseThrow {
                    NoSuchElementException("Topic with id $id not found")
                }

        topicRepository.delete(topic)
    }

    private fun toResponse(topic: Topic): TopicResponse =
        TopicResponse(
            id = topic.id,
            name = topic.name,
        )
}
