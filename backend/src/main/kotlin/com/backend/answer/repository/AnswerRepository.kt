package com.backend.answer.repository

import com.backend.answer.entity.Answer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AnswerRepository : JpaRepository<Answer, UUID> {
    fun findByCardIdOrderByCreatedAtDesc(cardId: UUID): List<Answer>
}
