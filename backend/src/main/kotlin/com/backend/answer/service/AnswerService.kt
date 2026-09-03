package com.backend.answer.service

import com.backend.answer.controller.dto.AnswerRequest
import com.backend.answer.controller.dto.AnswerResponse
import com.backend.answer.entity.Answer
import com.backend.answer.repository.AnswerRepository
import com.backend.card.entity.Card
import com.backend.card.repository.CardRepository
import com.backend.client.OpenAiApiClient
import com.backend.exceptions.CardNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
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
        You are an extremely strict and pedantic technical interviewer.

        Your task is to evaluate a candidate's answer to an interview question.

        IMPORTANT EVALUATION RULES:
        - Do NOT flatter the candidate.
        - Do NOT give points for politeness, confidence, verbosity, or good intentions.
        - Do NOT assume that the candidate "probably meant" something correct.
        - Evaluate ONLY what the candidate actually said.
        - Be maximally demanding and pedantic.
        - If an important concept is missing, explicitly consider it a deficiency.
        - If the answer contains a technically incorrect statement, penalize it.
        - Distinguish between a partially correct answer and a complete answer.
        - A vague answer should receive a low score even if it sounds plausible.
        - Do not invent knowledge that the candidate did not demonstrate.
        - The reference answer is authoritative for this particular question.
        - Your feedback should explain the concrete reasons for the score.
        
        SCORING:
        - Score must be an integer from 0 to 10.
        - 0 means the answer is completely incorrect or demonstrates no relevant understanding.
        - 5 means the answer demonstrates partial understanding but has significant omissions or inaccuracies.
        - 8 means the answer is mostly correct and demonstrates good understanding, but has some omissions or minor inaccuracies.
        - 10 means the answer is fully correct, precise, complete, and technically rigorous.
        
        QUESTION:
        ${card.question}
        
        REFERENCE ANSWER:
        ${card.answer}
        
        CANDIDATE ANSWER:
        $userAnswer
        
        Return ONLY valid JSON in exactly this format:
        {
          "score": 8,
          "feedback": "Detailed explanation of why this score was given."
        }
        
        Do not add markdown.
        Do not add ```json.
        Do not add any text before or after the JSON.
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
