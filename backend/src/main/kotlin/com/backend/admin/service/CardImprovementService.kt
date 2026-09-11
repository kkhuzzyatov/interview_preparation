package com.backend.admin.service

import com.backend.answer.dto.ImproveAnswerResponse
import com.backend.card.repository.CardRepository
import com.backend.client.OpenAiApiClient
import com.backend.properties.AdminProperties
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CardImprovementService(
    private val cardRepository: CardRepository,
    private val openAiApiClient: OpenAiApiClient,
    private val adminProperties: AdminProperties,
) {

    fun isValidAdminKey(key: String?): Boolean {
        return key != null && key == adminProperties.cardsImprovementKey
    }

    fun improveAnswer(cardId: UUID): ImproveAnswerResponse {
        val card =
            cardRepository
                .findById(cardId)
                .orElseThrow { NoSuchElementException("Card not found: $cardId") }

        val result =
            openAiApiClient.checkAndImproveAnswer(
                question = card.question,
                answer = card.answer,
            )

        if (!result.shouldImprove) {
            return ImproveAnswerResponse(
                cardId = card.id,
                message = "Card is already good",
                answer = card.answer,
            )
        }

        val improvedAnswer =
            result.improvedAnswer
                ?: throw IllegalStateException(
                    "OpenAI marked the answer for improvement but returned no improved answer",
                )

        card.answer = improvedAnswer
        cardRepository.save(card)

        return ImproveAnswerResponse(
            cardId = card.id,
            message = "Card answer improved",
            answer = improvedAnswer,
        )
    }
}