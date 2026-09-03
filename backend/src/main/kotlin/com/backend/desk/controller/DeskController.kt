package com.backend.desk.controller

import com.backend.desk.controller.dto.DeskResponse
import com.backend.desk.controller.dto.DeskWithCardsResponse
import com.backend.desk.service.DeskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/desks")
class DeskController(
    private val deskService: DeskService,
) {
    @Operation(summary = "Get all desks")
    @ApiResponse(
        responseCode = DeskApiCodes.OK,
        description = DeskApiMessages.OK,
    )
    @GetMapping
    fun getAll(): ResponseEntity<List<DeskResponse>> = ResponseEntity.ok(deskService.getAll())

    @Operation(summary = "Get a desk with its cards")
    @ApiResponses(
        ApiResponse(
            responseCode = DeskApiCodes.OK,
            description = DeskApiMessages.OK,
        ),
        ApiResponse(
            responseCode = DeskApiCodes.NOT_FOUND,
            description = DeskApiMessages.NOT_FOUND,
        ),
    )
    @GetMapping("/{deskId}")
    fun getByIdWithCards(
        @PathVariable deskId: UUID,
    ): ResponseEntity<DeskWithCardsResponse> = ResponseEntity.ok(deskService.getByIdWithCards(deskId))
}
