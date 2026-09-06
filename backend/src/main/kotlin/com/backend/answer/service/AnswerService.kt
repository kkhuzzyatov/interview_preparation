package com.backend.answer.service

import com.backend.answer.controller.dto.AnswerRequest
import com.backend.answer.controller.dto.AnswerResponse
import com.backend.answer.controller.dto.RevealAnswerResponse
import com.backend.answer.entity.Answer
import com.backend.answer.repository.AnswerRepository
import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.client.OpenAiApiClient
import com.backend.exceptions.CardNotFoundException
import com.backend.exceptions.UserIsNotExistException
import com.backend.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.security.Principal
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Service
class AnswerService(
    private val cardRepository: CardRepository,
    private val answerRepository: AnswerRepository,
    private val userRepository: UserRepository,
    private val openAiApiClient: OpenAiApiClient,
    private val objectMapper: ObjectMapper,
    private val clock: Clock,
) {
    @Transactional
    fun answer(
        cardId: UUID,
        request: AnswerRequest,
        principal: Principal,
    ): AnswerResponse {
        val user = getUser(principal)

        val card = getCard(cardId)

        val prompt =
            buildPrompt(
                card = card,
                userAnswer = request.answer,
            )

        val aiResponse = openAiApiClient.sendPrompt(prompt)

        val evaluation = parseEvaluation(aiResponse)

        val answer =
            Answer(
                id = UUID.randomUUID(),
                user = user,
                card = card,
                score = evaluation.score,
                createdAt = LocalDateTime.now(clock),
            )

        answerRepository.save(answer)

        return AnswerResponse(
            score = evaluation.score,
            feedback = evaluation.feedback,
            correctAnswer = card.answer,
        )
    }

    @Transactional
    fun reveal(
        cardId: UUID,
        principal: Principal,
    ): RevealAnswerResponse {
        val user = getUser(principal)

        val card = getCard(cardId)

        val answer =
            Answer(
                id = UUID.randomUUID(),
                user = user,
                card = card,
                score = 0,
                createdAt = LocalDateTime.now(clock),
            )

        answerRepository.save(answer)

        return RevealAnswerResponse(
            correctAnswer = card.answer,
        )
    }

    private fun getUser(principal: Principal) =
        userRepository
            .findById(UUID.fromString(principal.name))
            .orElseThrow {
                UserIsNotExistException(
                    "Authenticated user ${principal.name} does not exist",
                )
            }

    private fun getCard(cardId: UUID): Card =
        cardRepository
            .findById(cardId)
            .orElseThrow {
                CardNotFoundException(cardId)
            }

    private fun buildPrompt(
        card: Card,
        userAnswer: String,
    ): String =
        """
        Строго оцени ответ кандидата на технический вопрос.
        
        Оценивай только явно продемонстрированные знания. Не додумывай и не засчитывай неупомянутое. Эталон — главный критерий. Учитывай ошибки, неточности, пропуски важных понятий и неполноту. Расплывчатые формулировки оценивай ниже точных.
        
        Шкала 0–10:
        0 — неверно/понимания нет; 1–4 — слабый ответ с существенными ошибками; 5 — частичное понимание; 6–7 — в целом верно, но есть важные пробелы; 8 — хороший ответ с небольшими недостатками; 9 — почти полный и точный; 10 — полный и технически строгий.
        
        Вопрос: ${card.question}
        Эталон: ${card.answer}
        Ответ: $userAnswer
        
        Верни ТОЛЬКО JSON:
        {"score":0,"feedback":"Максимум 2 коротких предложения на русском с главными причинами оценки."}
        """.trimIndent()

    private fun parseEvaluation(response: String): AiEvaluation =
        try {
            val evaluation =
                objectMapper.readValue(
                    response,
                    AiEvaluation::class.java,
                )

            require(evaluation.score in 0..10) {
                "AI returned invalid score: ${evaluation.score}"
            }

            require(evaluation.feedback.isNotBlank()) {
                "AI returned empty feedback"
            }

            evaluation
        } catch (exception: Exception) {
            throw IllegalStateException(
                "Failed to parse OpenAI evaluation: $response",
                exception,
            )
        }
}
