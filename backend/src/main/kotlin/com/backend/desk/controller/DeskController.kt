package com.backend.desk.controller

import com.backend.desk.controller.dto.CreateDeskRequest
import com.backend.desk.controller.dto.DeskResponse
import com.backend.desk.controller.dto.DeskWithCardsResponse
import com.backend.desk.controller.dto.UpdateDeskRequest
import com.backend.desk.service.DeskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/desks")
class DeskController(
    private val deskService: DeskService,
) {
    private val log = LoggerFactory.getLogger(DeskController::class.java)

    @Operation(summary = "Get all desks")
    @ApiResponse(
        responseCode = DeskApiCodes.OK,
        description = DeskApiMessages.OK,
    )
    @GetMapping
    fun getAll(): ResponseEntity<List<DeskResponse>> {
        log
            .atInfo()
            .log("Getting all desks")

        return ResponseEntity.ok(
            deskService.getAll(),
        )
    }

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
    ): ResponseEntity<DeskWithCardsResponse> {
        log
            .atInfo()
            .addKeyValue("deskId", deskId)
            .log("Getting desk with cards")

        return ResponseEntity.ok(
            deskService.getByIdWithCards(deskId),
        )
    }

    @Operation(summary = "Create a desk")
    @ApiResponse(
        responseCode = DeskApiCodes.CREATED,
        description = DeskApiMessages.CREATED,
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateDeskRequest,
    ): ResponseEntity<DeskResponse> {
        log
            .atInfo()
            .addKeyValue("name", request.name)
            .addKeyValue("topicId", request.topicId)
            .log("Creating desk")

        val response = deskService.create(request)

        return ResponseEntity
            .status(201)
            .body(response)
    }

    @Operation(summary = "Update a desk")
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
    @PutMapping("/{deskId}")
    fun update(
        @PathVariable deskId: UUID,
        @Valid @RequestBody request: UpdateDeskRequest,
    ): ResponseEntity<DeskResponse> {
        log
            .atInfo()
            .addKeyValue("deskId", deskId)
            .log("Updating desk")

        return ResponseEntity.ok(
            deskService.update(
                id = deskId,
                request = request,
            ),
        )
    }

    @Operation(summary = "Delete a desk")
    @ApiResponses(
        ApiResponse(
            responseCode = DeskApiCodes.NO_CONTENT,
            description = DeskApiMessages.NO_CONTENT,
        ),
        ApiResponse(
            responseCode = DeskApiCodes.NOT_FOUND,
            description = DeskApiMessages.NOT_FOUND,
        ),
    )
    @DeleteMapping("/{deskId}")
    fun delete(
        @PathVariable deskId: UUID,
    ): ResponseEntity<Void> {
        log
            .atInfo()
            .addKeyValue("deskId", deskId)
            .log("Deleting desk")

        deskService.delete(deskId)

        return ResponseEntity.noContent().build()
    }
}
