package com.backend.review.controller

import com.backend.review.controller.dto.NextCardRequest
import com.backend.review.controller.dto.ReviewCardResponse
import com.backend.review.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/review")
class ReviewController(
    private val reviewService: ReviewService,
) {
    private val log = LoggerFactory.getLogger(ReviewController::class.java)

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
    @PostMapping("/next")
    fun getNextCard(
        principal: Principal,
        @RequestBody request: NextCardRequest,
    ): ResponseEntity<ReviewCardResponse> {
        val card = reviewService.getNextCard(principal, request.deskIds)

        log
            .atInfo()
            .addKeyValue("cardId", card.id)
            .addKeyValue("deskId", card.desk.id)
            .log("Getting next card for review")

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
