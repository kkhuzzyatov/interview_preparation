package com.backend.answer.service

import com.backend.answer.controller.dto.AnswerHistoryResponse
import com.backend.answer.controller.dto.AnswerRequest
import com.backend.answer.controller.dto.AnswerResponse
import com.backend.answer.controller.dto.RevealAnswerResponse
import com.backend.answer.entity.Answer
import com.backend.answer.entity.AnswerProcessing
import com.backend.answer.entity.AnswerProcessingStatus
import com.backend.answer.mapper.AnswerMapper
import com.backend.answer.repository.AnswerProcessingRepository
import com.backend.answer.repository.AnswerRepository
import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.client.OpenAiApiClient
import com.backend.exceptions.CardNotFoundException
import com.backend.exceptions.UserIsNotExistException
import com.backend.user.entity.User
import com.backend.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import java.security.Principal
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Service
class AnswerService(
    private val cardRepository: CardRepository,
    private val answerRepository: AnswerRepository,
    private val userRepository: UserRepository,
    private val answerProcessingRepository: AnswerProcessingRepository,
    private val openAiApiClient: OpenAiApiClient,
    private val answerMapper: AnswerMapper,
    private val objectMapper: ObjectMapper,
    private val clock: Clock,
) {
    fun start(
        cardId: UUID,
        principal: Principal,
    ) {
        val user = getUser(principal)
        val card = getCard(cardId)

        check(answerProcessingRepository.findByUserId(user.id) == null) {
            "User ${user.id} already has an answer in progress"
        }

        answerProcessingRepository.save(
            AnswerProcessing(
                answerId = UUID.randomUUID(),
                userId = user.id,
                cardId = card.id,
                startAnswerTime = Instant.now(clock),
                status = AnswerProcessingStatus.QUESTION_SENT,
            ),
        )
    }

    fun answer(
        cardId: UUID,
        request: AnswerRequest,
        principal: Principal,
    ): AnswerResponse {
        val user = getUser(principal)
        val card = getCard(cardId)

        val processing =
            answerProcessingRepository.findByUserId(user.id)
                ?: error(
                    "No answer in progress for user ${user.id}",
                )

        validateProcessing(
            processing = processing,
            cardId = cardId,
        )

        val submissionTime = Instant.now(clock)

        val processingForAi =
            processing.copy(
                userAnswer = request.answer,
                submissionTime = submissionTime,
                aiProcessingStartTime = submissionTime,
                status = AnswerProcessingStatus.AI_PROCESSING,
            )

        answerProcessingRepository.save(processingForAi)

        val prompt =
            buildPrompt(
                card = card,
                userAnswer = request.answer,
            )

        val aiResponse = openAiApiClient.sendPrompt(prompt)
        val evaluation = parseEvaluation(aiResponse)

        val aiCompletedTime = Instant.now(clock)

        val aiProcessingDurationMs =
            Duration
                .between(
                    submissionTime,
                    aiCompletedTime,
                ).toMillis()

        saveCompletedAnswer(
            processing = processingForAi,
            evaluation = evaluation,
            aiProcessingDurationMs = aiProcessingDurationMs,
            user = user,
            card = card,
        )

        answerProcessingRepository.deleteByAnswerId(
            processingForAi.answerId,
        )

        return AnswerResponse(
            score = evaluation.score,
            feedback = evaluation.feedback,
            correctAnswer = card.answer,
        )
    }

    fun reveal(
        cardId: UUID,
        principal: Principal,
    ): RevealAnswerResponse {
        val user = getUser(principal)
        val card = getCard(cardId)

        val processing =
            answerProcessingRepository.findByUserId(user.id)

        if (processing != null) {
            check(processing.cardId == cardId) {
                "Answer does not belong to card $cardId"
            }

            answerProcessingRepository.deleteByAnswerId(
                processing.answerId,
            )
        }

        return RevealAnswerResponse(
            correctAnswer = card.answer,
        )
    }

    @Transactional(readOnly = true)
    fun getAll(principal: Principal): List<AnswerHistoryResponse> {
        val user = getUser(principal)

        return answerRepository
            .findByUserIdOrderByCreatedAtDesc(user.id)
            .map(answerMapper::toHistoryResponse)
    }

    private fun saveCompletedAnswer(
        processing: AnswerProcessing,
        evaluation: AiEvaluation,
        aiProcessingDurationMs: Long,
        user: User,
        card: Card,
    ) {
        val submissionTime =
            requireNotNull(processing.submissionTime) {
                "Submission time is missing for ${processing.answerId}"
            }

        val userAnswer =
            requireNotNull(processing.userAnswer) {
                "User answer is missing for ${processing.answerId}"
            }

        answerRepository.save(
            Answer(
                id = processing.answerId,
                user = user,
                card = card,
                userAnswer = userAnswer,
                aiFeedback = evaluation.feedback,
                startAnswerTime =
                    processing.startAnswerTime
                        .atZone(clock.zone)
                        .toLocalDateTime(),
                submissionTime =
                    submissionTime
                        .atZone(clock.zone)
                        .toLocalDateTime(),
                aiProcessingDurationMs = aiProcessingDurationMs,
                score = evaluation.score,
                createdAt = LocalDateTime.now(clock),
            ),
        )
    }

    private fun validateProcessing(
        processing: AnswerProcessing,
        cardId: UUID,
    ) {
        check(processing.cardId == cardId) {
            "Answer does not belong to card $cardId"
        }

        check(
            processing.status ==
                AnswerProcessingStatus.QUESTION_SENT,
        ) {
            "Answer ${processing.answerId} is not waiting for user input"
        }
    }

    private fun getUser(principal: Principal): User =
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
        0 — неверно/понимания нет;
        1–4 — слабый ответ с существенными ошибками;
        5 — частичное понимание;
        6–7 — в целом верно, но есть важные пробелы;
        8 — хороший ответ с небольшими недостатками;
        9 — почти полный и точный;
        10 — полный и технически строгий.

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
