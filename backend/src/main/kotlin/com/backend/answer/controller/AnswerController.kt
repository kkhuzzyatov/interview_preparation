package com.backend.answer.controller

import com.backend.answer.controller.dto.AnswerRequest
import com.backend.answer.controller.dto.AnswerResponse
import com.backend.answer.controller.dto.RevealAnswerResponse
import com.backend.answer.service.AnswerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/api/answer")
class AnswerController(
    private val answerService: AnswerService,
) {
    @Operation(summary = "Evaluate an answer to a card")
    @ApiResponses(
        ApiResponse(
            responseCode = AnswerApiCodes.OK,
            description = AnswerApiMessages.OK,
        ),
        ApiResponse(
            responseCode = AnswerApiCodes.UNAUTHORIZED,
            description = AnswerApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = AnswerApiCodes.NOT_FOUND,
            description = AnswerApiMessages.NOT_FOUND,
        ),
    )
    @PostMapping("/{cardId}")
    fun answer(
        principal: Principal,
        @PathVariable cardId: UUID,
        @RequestBody request: AnswerRequest,
    ): ResponseEntity<AnswerResponse> =
        ResponseEntity.ok(
            answerService.answer(
                cardId = cardId,
                request = request,
                principal = principal,
            ),
        )

    @Operation(summary = "Reveal the correct answer without AI evaluation")
    @ApiResponses(
        ApiResponse(
            responseCode = AnswerApiCodes.OK,
            description = AnswerApiMessages.OK,
        ),
        ApiResponse(
            responseCode = AnswerApiCodes.UNAUTHORIZED,
            description = AnswerApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = AnswerApiCodes.NOT_FOUND,
            description = AnswerApiMessages.NOT_FOUND,
        ),
    )
    @PostMapping("/{cardId}/reveal")
    fun reveal(
        principal: Principal,
        @PathVariable cardId: UUID,
    ): ResponseEntity<RevealAnswerResponse> =
        ResponseEntity.ok(
            answerService.reveal(
                cardId = cardId,
                principal = principal,
            ),
        )
}
