package com.backend.answer.repository

import com.backend.answer.entity.AnswerProcessing
import java.util.UUID

interface AnswerProcessingRepository {
    fun save(processing: AnswerProcessing)

    fun findByAnswerId(answerId: UUID): AnswerProcessing?

    fun findByUserId(userId: UUID): AnswerProcessing?

    fun deleteByAnswerId(answerId: UUID)
}
