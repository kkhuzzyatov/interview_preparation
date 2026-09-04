package com.backend.answer.service

import com.backend.answer.controller.dto.AnswerRequest
import com.backend.answer.controller.dto.AnswerResponse
import com.backend.answer.entity.Answer
import com.backend.answer.repository.AnswerRepository
import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.client.OpenAiApiClient
import com.backend.exceptions.CardNotFoundException
import tools.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Service
class AnswerService(
    private val cardRepository: CardRepository,
    private val answerRepository: AnswerRepository,
    private val openAiApiClient: OpenAiApiClient,
    private val objectMapper: ObjectMapper,
    private val clock: Clock,
) {
    @Transactional
    fun answer(
        cardId: UUID,
        request: AnswerRequest,
    ): AnswerResponse {
        val card =
            cardRepository
                .findById(cardId)
                .orElseThrow { CardNotFoundException(cardId) }

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

    private fun buildPrompt(
        card: Card,
        userAnswer: String,
    ): String =
        """
        Ты — максимально строгий и дотошный технический интервьюер.

        Твоя задача — объективно оценить ответ кандидата на технический вопрос.

        ПРАВИЛА ОЦЕНКИ:
        - Не подлизывайся кандидату.
        - Не завышай оценку из желания быть вежливым или поддержать кандидата.
        - Не додумывай за кандидата то, что он не сказал.
        - Оценивай только те знания, которые кандидат реально продемонстрировал в своём ответе.
        - Будь максимально придирчивым к технической точности формулировок.
        - Если важное понятие не упомянуто, считай это недостатком ответа.
        - Если кандидат допустил техническую ошибку, обязательно учитывай её при выставлении оценки.
        - Отличай частично правильный ответ от полного и точного ответа.
        - Расплывчатые и общие формулировки оценивай низко, даже если они звучат правдоподобно.
        - Не засчитывай знания, которые кандидат мог иметь в виду, но не продемонстрировал.
        - Не пытайся найти оправдание ошибкам кандидата.
        - Эталонный ответ является основным ориентиром для оценки.
        - В feedback обязательно указывай конкретные причины выставленной оценки.
        - Если ответ кандидата правильный, всё равно проверь его на полноту и техническую точность.
        - Не хвали кандидата без конкретной причины.

        ШКАЛА ОЦЕНКИ:
        - 0 — ответ полностью неправильный или кандидат не продемонстрировал понимания темы.
        - 1–2 — крайне слабое понимание, существенные ошибки.
        - 3–4 — есть отдельные правильные мысли, но ответ в целом слабый и содержит существенные пробелы или ошибки.
        - 5 — частичное понимание темы, но есть существенные пробелы или неточности.
        - 6–7 — в целом правильный ответ, но отсутствуют важные детали или присутствуют заметные неточности.
        - 8 — хороший и в основном правильный ответ, но есть отдельные упущения или небольшие неточности.
        - 9 — практически полный, точный и технически грамотный ответ с незначительными недостатками.
        - 10 — полный, точный и технически строгий ответ без существенных недостатков.

        ВОПРОС:
        ${card.question}

        ЭТАЛОННЫЙ ОТВЕТ:
        ${card.answer}

        ОТВЕТ КАНДИДАТА:
        $userAnswer

        Верни результат проверки ТОЛЬКО в формате JSON:

        {
          "score": 8,
          "feedback": "Подробное объяснение выставленной оценки с указанием конкретных правильных моментов, ошибок и недостающих знаний."
        }

        Требования к формату:
        - score должен быть целым числом от 0 до 10.
        - feedback должен быть на русском языке.
        - Не используй Markdown.
        - Не оборачивай JSON в ```json.
        - Не добавляй никакого текста до или после JSON.
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
