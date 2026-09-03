package com.backend.desk.service

import com.backend.desk.controller.dto.DeskResponse
import com.backend.desk.controller.dto.DeskWithCardsResponse
import com.backend.desk.mapper.DeskMapper
import com.backend.desk.repository.DeskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeskService(
    private val deskRepository: DeskRepository,
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
            .orElseThrow { NoSuchElementException("Desk with id $id not found") }
}
