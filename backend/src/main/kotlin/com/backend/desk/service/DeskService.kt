package com.backend.desk.service

import com.backend.desk.controller.dto.CreateDeskRequest
import com.backend.desk.controller.dto.DeskResponse
import com.backend.desk.controller.dto.DeskWithCardsResponse
import com.backend.desk.controller.dto.UpdateDeskRequest
import com.backend.desk.entity.Desk
import com.backend.desk.mapper.DeskMapper
import com.backend.desk.repository.DeskRepository
import com.backend.topic.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeskService(
    private val deskRepository: DeskRepository,
    private val topicRepository: TopicRepository,
) {
    @Transactional(readOnly = true)
    fun getAll(): List<DeskResponse> =
        deskRepository
            .findAll()
            .map(DeskMapper::toResponse)

    @Transactional(readOnly = true)
    fun getByIdWithCards(id: UUID): DeskWithCardsResponse =
        deskRepository
            .findWithCardsById(id)
            .map(DeskMapper::toResponseWithCards)
            .orElseThrow {
                NoSuchElementException("Desk with id $id not found")
            }

    @Transactional
    fun create(request: CreateDeskRequest): DeskResponse {
        val topic =
            topicRepository
                .findById(request.topicId)
                .orElseThrow {
                    NoSuchElementException(
                        "Topic with id ${request.topicId} not found",
                    )
                }

        val desk =
            Desk(
                id = UUID.randomUUID(),
                name = request.name,
                topic = topic,
            )

        return DeskMapper.toResponse(
            deskRepository.save(desk),
        )
    }

    @Transactional
    fun update(
        id: UUID,
        request: UpdateDeskRequest,
    ): DeskResponse {
        val desk =
            deskRepository
                .findById(id)
                .orElseThrow {
                    NoSuchElementException("Desk with id $id not found")
                }

        val topic =
            topicRepository
                .findById(request.topicId)
                .orElseThrow {
                    NoSuchElementException(
                        "Topic with id ${request.topicId} not found",
                    )
                }

        desk.name = request.name
        desk.topic = topic

        return DeskMapper.toResponse(
            deskRepository.save(desk),
        )
    }

    @Transactional
    fun delete(id: UUID) {
        val desk =
            deskRepository
                .findById(id)
                .orElseThrow {
                    NoSuchElementException("Desk with id $id not found")
                }

        deskRepository.delete(desk)
    }
}
