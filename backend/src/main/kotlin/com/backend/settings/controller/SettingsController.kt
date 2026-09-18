package com.backend.settings.controller

import com.backend.settings.controller.dto.request.SettingsRequestDto
import com.backend.settings.controller.dto.response.SettingsResponseDto
import com.backend.settings.service.SettingsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/settings")
class SettingsController(
    private val settingsService: SettingsService,
) {
    @Operation(
        summary = "Get application settings",
        description = "Returns the current application settings.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = SettingsApiCodes.OK,
                description = SettingsApiMessages.OK,
            ),
            ApiResponse(
                responseCode = SettingsApiCodes.NOT_FOUND,
                description = SettingsApiMessages.NOT_FOUND,
            ),
        ],
    )
    @GetMapping
    fun getSettings(): ResponseEntity<SettingsResponseDto> = ResponseEntity.ok(settingsService.get())

    @Operation(
        summary = "Update application settings",
        description = "Clears the current settings and replaces them with the provided settings.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = SettingsApiCodes.OK,
                description = SettingsApiMessages.OK,
            ),
            ApiResponse(
                responseCode = SettingsApiCodes.UNAUTHORIZED,
                description = SettingsApiMessages.UNAUTHORIZED,
            ),
            ApiResponse(
                responseCode = SettingsApiCodes.FORBIDDEN,
                description = SettingsApiMessages.FORBIDDEN,
            ),
        ],
    )
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun updateSettings(
        @RequestBody request: SettingsRequestDto,
    ): ResponseEntity<Void> {
        settingsService.update(request)
        return ResponseEntity.ok().build()
    }
}
