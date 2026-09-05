package com.backend.user.controller

import com.backend.user.controller.dto.RegisterRequest
import com.backend.user.entity.User
import com.backend.user.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/user")
class UserController(
    private val authService: AuthService,
) {
    @Operation(summary = "Register a new user")
    @ApiResponses(
        ApiResponse(
            responseCode = UserApiCodes.OK,
            description = UserApiMessages.OK,
        ),
        ApiResponse(
            responseCode = UserApiCodes.BAD_REQUEST,
            description = UserApiMessages.BAD_REQUEST,
        ),
    )
    @PostMapping
    fun register(
        @RequestBody request: RegisterRequest,
    ): ResponseEntity<User> =
        ResponseEntity.ok(
            authService.register(
                request.email,
                request.password,
            ),
        )

    @Operation(summary = "Get current user")
    @ApiResponses(
        ApiResponse(
            responseCode = UserApiCodes.OK,
            description = UserApiMessages.OK,
        ),
        ApiResponse(
            responseCode = UserApiCodes.NOT_FOUND,
            description = UserApiMessages.NOT_FOUND,
        ),
    )
    @GetMapping
    fun getMe(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<User> =
        ResponseEntity.ok(
            authService.getMyUuid(userId),
        )
}
