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
                ?: AnswerProcessing(
                    answerId = UUID.randomUUID(),
                    userId = user.id,
                    cardId = card.id,
                    startAnswerTime = Instant.now(clock),
                    status = AnswerProcessingStatus.QUESTION_SENT,
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

        val processing = answerProcessingRepository.findByUserId(user.id)

        if (processing != null) {
            check(processing.cardId == cardId) {
                "Answer does not belong to card $cardId"
            }

            saveRevealedAnswer(
                processing = processing,
                user = user,
                card = card,
            )

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

    private fun saveRevealedAnswer(
        processing: AnswerProcessing,
        user: User,
        card: Card,
    ) {
        val revealTime = Instant.now(clock)

        answerRepository.save(
            Answer(
                id = processing.answerId,
                user = user,
                card = card,
                userAnswer = "The answer was revealed.",
                aiFeedback = "The answer was revealed.",
                startAnswerTime =
                    processing.startAnswerTime
                        .atZone(clock.zone)
                        .toLocalDateTime(),
                submissionTime =
                    revealTime
                        .atZone(clock.zone)
                        .toLocalDateTime(),
                aiProcessingDurationMs = 0,
                score = 0,
                createdAt = LocalDateTime.now(clock),
            ),
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
            Строго, но справедливо оцени ответ кандидата на технический вопрос 
            с позиции уровня Middle Java Developer.
        
            Оценивай ТОЛЬКО знания, явно продемонстрированные в ответе.
            Не додумывай знания кандидата и не засчитывай то, чего он не сказал.
        
            Эталон используй как ориентир для определения ключевых аспектов ответа,
            но НЕ требуй дословного или полного воспроизведения эталона.
            Отсутствие второстепенных деталей не должно существенно снижать оценку,
            если кандидат правильно объяснил основную концепцию.
        
            При оценке учитывай в первую очередь:
            1. Корректность — есть ли фактические или концептуальные ошибки.
            2. Понимание — демонстрирует ли ответ понимание принципа, а не просто термин.
            3. Ключевые аспекты — раскрыты ли основные идеи, необходимые для ответа.
            4. Точность формулировок — насколько ответ технически корректен и однозначен.
            5. Глубину — есть ли релевантные нюансы, важные для Middle.
        
            ВАЖНО:
            - Не штрафуй существенно за краткость, если краткий ответ корректно передаёт
              суть вопроса.
            - Не требуй перечисления всех возможных методов, исключений, нюансов и edge cases,
              если вопрос этого явно не требует.
            - Отличай отсутствие детали от фактической ошибки: ошибка должна снижать оценку
              сильнее, чем пропуск второстепенной информации.
            - Если ответ содержит правильную основную идею, но неполон, обычно оценивай его
              как 6–8, а не 3–5.
            - Если ответ содержит фундаментальную концептуальную ошибку, оценка обычно не выше 4,
              даже если присутствуют отдельные правильные детали.
            - Если ответ частично правильный, оценивай именно продемонстрированные знания,
              а не то, насколько он совпадает с эталоном по объёму.
            - Не требуй знаний уровня Senior/эксперта, если они не нужны для корректного ответа
              на вопрос Middle-разработчика.
            - Не учитывай орфографические и стилистические ошибки, если они не мешают понять
              технический смысл ответа.
        
            Шкала 0–10:
        
            0 — ответа по существу нет или ответ полностью неверный.
            1–2 — очень слабое понимание; существенная часть ответа неверна.
            3–4 — есть отдельные правильные элементы, но присутствуют существенные
                  концептуальные ошибки или отсутствует понимание основной идеи.
            5 — частичное понимание; основная идея затронута, но ответ заметно неполный
                или содержит существенные неточности.
            6 — в целом правильное базовое понимание, но не хватает важных аспектов
                или есть заметные неточности.
            7 — хороший корректный ответ для Middle, но отсутствует часть релевантных
                деталей или глубины.
            8 — сильный ответ: основная концепция раскрыта точно, есть ключевые нюансы;
                присутствуют только небольшие пробелы.
            9 — почти полный, точный и хорошо сформулированный ответ уровня Middle+/Senior.
            10 — исчерпывающий, технически строгий ответ с глубоким пониманием темы
                 и без существенных недостатков.
        
            При выборе оценки сначала определи:
            - основную идею вопроса;
            - какие части ответа кандидата корректны;
            - какие утверждения ошибочны;
            - какие важные аспекты действительно отсутствуют.
        
            Затем выставь ОДНУ итоговую оценку 0–10.
            Не завышай оценку за отдельные правильные факты, если основная концепция неверна.
            Не занижай оценку только за отсутствие второстепенных деталей.
        
            Feedback должен кратко объяснять именно причины выставленного балла:
            сначала укажи, что кандидат ответил правильно, затем главный недостаток.
            Не перечисляй все возможные недочёты.
        
            Вопрос: ${card.question}
            Эталон: ${card.answer}
            Ответ кандидата: $userAnswer
        
            Верни ТОЛЬКО валидный JSON без markdown:
            {"score":0,"feedback":"Максимум 2 коротких предложения на русском."}
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
