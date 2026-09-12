package com.backend.card.controller

import com.backend.card.controller.dto.UpdateCardRequest
import com.backend.card.controller.dto.UpdateCardResponse
import com.backend.card.service.CardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/cards")
class CardController(
    private val cardService: CardService,
) {
    @Operation(summary = "Update a card")
    @ApiResponses(
        ApiResponse(
            responseCode = CardApiCodes.OK,
            description = CardApiMessages.OK,
        ),
        ApiResponse(
            responseCode = CardApiCodes.UNAUTHORIZED,
            description = CardApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = CardApiCodes.NOT_FOUND,
            description = CardApiMessages.NOT_FOUND,
        ),
    )
    @PutMapping("/{cardId}")
    fun updateCard(
        @PathVariable cardId: UUID,
        @RequestBody request: UpdateCardRequest,
    ): ResponseEntity<UpdateCardResponse> {
        val card =
            cardService.updateCard(
                cardId = cardId,
                question = request.question,
                answer = request.answer,
            )

        return ResponseEntity.ok(
            UpdateCardResponse(
                id = card.id,
                question = card.question,
                answer = card.answer,
                deskId = card.desk.id,
            ),
        )
    }
}
