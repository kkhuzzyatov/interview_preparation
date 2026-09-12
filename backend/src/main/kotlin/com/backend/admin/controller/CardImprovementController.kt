package com.backend.admin.controller

import com.backend.admin.service.CardImprovementService
import com.backend.answer.dto.ImproveAnswerResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/admin/cards")
class CardImprovementController(
    private val cardImprovementService: CardImprovementService,
) {
    @Operation(summary = "Improve the correct answer of a card using AI")
    @ApiResponses(
        ApiResponse(
            responseCode = CardImprovementApiCodes.OK,
            description = CardImprovementApiMessages.OK,
        ),
        ApiResponse(
            responseCode = CardImprovementApiCodes.UNAUTHORIZED,
            description = CardImprovementApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = CardImprovementApiCodes.NOT_FOUND,
            description = CardImprovementApiMessages.NOT_FOUND,
        ),
    )
    @PostMapping("/{cardId}/improve-answer")
    fun improveAnswer(
        @PathVariable cardId: UUID,
        @RequestHeader("X-Admin-Key", required = false) adminKey: String?,
    ): ResponseEntity<ImproveAnswerResponse> {
        if (!cardImprovementService.isValidAdminKey(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val result = cardImprovementService.improveAnswer(cardId)

        return ResponseEntity.ok(result)
    }
}
