package com.backend.topic.controller

import com.backend.topic.controller.dto.CreateTopicRequest
import com.backend.topic.controller.dto.TopicResponse
import com.backend.topic.controller.dto.UpdateTopicRequest
import com.backend.topic.service.TopicService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
@RequestMapping("/api/topics")
@PreAuthorize("hasRole('ADMIN')")
class TopicController(
    private val topicService: TopicService,
) {
    private val log = LoggerFactory.getLogger(TopicController::class.java)

    @Operation(summary = "Get all topics")
    @ApiResponse(
        responseCode = TopicApiCodes.OK,
        description = TopicApiMessages.OK,
    )
    @GetMapping
    fun getAll(): ResponseEntity<List<TopicResponse>> {
        log
            .atInfo()
            .log("Getting all topics")

        return ResponseEntity.ok(topicService.getAll())
    }

    @Operation(summary = "Get topic by id")
    @ApiResponses(
        ApiResponse(
            responseCode = TopicApiCodes.OK,
            description = TopicApiMessages.OK,
        ),
        ApiResponse(
            responseCode = TopicApiCodes.NOT_FOUND,
            description = TopicApiMessages.NOT_FOUND,
        ),
    )
    @GetMapping("/{topicId}")
    fun getById(
        @PathVariable topicId: UUID,
    ): ResponseEntity<TopicResponse> {
        log
            .atInfo()
            .addKeyValue("topicId", topicId)
            .log("Getting topic")

        return ResponseEntity.ok(
            topicService.getById(topicId),
        )
    }

    @Operation(summary = "Create a topic")
    @ApiResponse(
        responseCode = TopicApiCodes.CREATED,
        description = TopicApiMessages.CREATED,
    )
    @PostMapping
    fun create(
        @Valid
        @RequestBody
        request: CreateTopicRequest,
    ): ResponseEntity<TopicResponse> {
        log
            .atInfo()
            .log("Creating topic")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(topicService.create(request))
    }

    @Operation(summary = "Update a topic")
    @ApiResponses(
        ApiResponse(
            responseCode = TopicApiCodes.OK,
            description = TopicApiMessages.OK,
        ),
        ApiResponse(
            responseCode = TopicApiCodes.NOT_FOUND,
            description = TopicApiMessages.NOT_FOUND,
        ),
    )
    @PutMapping("/{topicId}")
    fun update(
        @PathVariable topicId: UUID,
        @Valid
        @RequestBody
        request: UpdateTopicRequest,
    ): ResponseEntity<TopicResponse> {
        log
            .atInfo()
            .addKeyValue("topicId", topicId)
            .log("Updating topic")

        return ResponseEntity.ok(
            topicService.update(topicId, request),
        )
    }

    @Operation(summary = "Delete a topic")
    @ApiResponses(
        ApiResponse(
            responseCode = TopicApiCodes.NO_CONTENT,
            description = TopicApiMessages.NO_CONTENT,
        ),
        ApiResponse(
            responseCode = TopicApiCodes.NOT_FOUND,
            description = TopicApiMessages.NOT_FOUND,
        ),
    )
    @DeleteMapping("/{topicId}")
    fun delete(
        @PathVariable topicId: UUID,
    ): ResponseEntity<Void> {
        log
            .atInfo()
            .addKeyValue("topicId", topicId)
            .log("Deleting topic")

        topicService.delete(topicId)

        return ResponseEntity.noContent().build()
    }
}
