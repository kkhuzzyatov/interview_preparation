package com.backend.settings.controller

import com.backend.settings.controller.dto.SettingsRequestDto
import com.backend.settings.repository.entity.ApplicationSettings
import com.backend.settings.service.ApplicationSettingsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/settings")
@PreAuthorize("hasRole('ADMIN')")
class ApplicationSettingsController(
    private val applicationSettingsService: ApplicationSettingsService,
) {
    @Operation(summary = "Get application settings")
    @ApiResponses(
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.OK,
            description = ApplicationSettingsApiMessages.OK,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.UNAUTHORIZED,
            description = ApplicationSettingsApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.FORBIDDEN,
            description = ApplicationSettingsApiMessages.FORBIDDEN,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.NOT_FOUND,
            description = ApplicationSettingsApiMessages.NOT_FOUND,
        ),
    )
    @GetMapping
    fun get(): ResponseEntity<ApplicationSettings> = ResponseEntity.ok(applicationSettingsService.get())

    @Operation(summary = "Create application settings")
    @ApiResponses(
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.CREATED,
            description = ApplicationSettingsApiMessages.CREATED,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.UNAUTHORIZED,
            description = ApplicationSettingsApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.FORBIDDEN,
            description = ApplicationSettingsApiMessages.FORBIDDEN,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.CONFLICT,
            description = ApplicationSettingsApiMessages.CONFLICT,
        ),
    )
    @PostMapping
    fun create(
        @RequestBody request: SettingsRequestDto,
    ): ResponseEntity<ApplicationSettings> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(applicationSettingsService.create(request))

    @Operation(summary = "Update application settings")
    @ApiResponses(
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.OK,
            description = ApplicationSettingsApiMessages.OK,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.UNAUTHORIZED,
            description = ApplicationSettingsApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.FORBIDDEN,
            description = ApplicationSettingsApiMessages.FORBIDDEN,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.NOT_FOUND,
            description = ApplicationSettingsApiMessages.NOT_FOUND,
        ),
    )
    @PutMapping
    fun update(
        @RequestBody request: SettingsRequestDto,
    ): ResponseEntity<ApplicationSettings> = ResponseEntity.ok(applicationSettingsService.update(request))

    @Operation(summary = "Delete application settings")
    @ApiResponses(
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.NO_CONTENT,
            description = ApplicationSettingsApiMessages.NO_CONTENT,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.UNAUTHORIZED,
            description = ApplicationSettingsApiMessages.UNAUTHORIZED,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.FORBIDDEN,
            description = ApplicationSettingsApiMessages.FORBIDDEN,
        ),
        ApiResponse(
            responseCode = ApplicationSettingsApiCodes.NOT_FOUND,
            description = ApplicationSettingsApiMessages.NOT_FOUND,
        ),
    )
    @DeleteMapping
    fun delete(): ResponseEntity<Void> {
        applicationSettingsService.delete()
        return ResponseEntity.noContent().build()
    }
}
