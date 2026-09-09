package com.backend.answer.mapper

import com.backend.answer.controller.dto.AnswerHistoryResponse
import com.backend.answer.entity.Answer
import org.springframework.stereotype.Component

@Component
class AnswerMapper {
    fun toHistoryResponse(answer: Answer): AnswerHistoryResponse =
        AnswerHistoryResponse(
            id = answer.id,
            cardId = answer.card.id,
            question = answer.card.question,
            correctAnswer = answer.card.answer,
            userAnswer = answer.userAnswer,
            aiFeedback = answer.aiFeedback,
            score = answer.score,
            startAnswerTime = answer.startAnswerTime,
            submissionTime = answer.submissionTime,
            aiProcessingDurationMs = answer.aiProcessingDurationMs,
            createdAt = answer.createdAt,
            deskId =
                answer.card.desk.id
                    .toString(),
            deskName = answer.card.desk.name,
        )
}
