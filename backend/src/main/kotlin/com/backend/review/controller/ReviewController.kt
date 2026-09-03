package com.backend.review.controller

import com.backend.review.controller.dto.ReviewCardResponse
import com.backend.review.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/review")
class ReviewController(
    private val reviewService: ReviewService,
) {
    @Operation(summary = "Get the next card for review")
    @ApiResponses(
        ApiResponse(
            responseCode = ReviewApiCodes.OK,
            description = ReviewApiMessages.OK,
        ),
        ApiResponse(
            responseCode = ReviewApiCodes.NOT_FOUND,
            description = ReviewApiMessages.NOT_FOUND,
        ),
    )
    @GetMapping("/next")
    fun getNextCard(): ResponseEntity<ReviewCardResponse> {
        val card = reviewService.getNextCard()

        val response =
            ReviewCardResponse(
                cardId = card.id,
                deskId = card.desk.id,
                deskName = card.desk.name,
                question = card.question,
            )

        return ResponseEntity.ok(response)
    }
}
