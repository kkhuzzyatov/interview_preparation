package com.backend.user.controller

import com.backend.user.controller.dto.LoginRequest
import com.backend.user.controller.dto.LoginResult
import com.backend.user.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "Login")
    @ApiResponses(
        ApiResponse(
            responseCode = AuthApiCodes.OK,
            description = AuthApiMessages.OK,
        ),
        ApiResponse(
            responseCode = AuthApiCodes.BAD_REQUEST,
            description = AuthApiMessages.BAD_REQUEST,
        ),
    )
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): ResponseEntity<LoginResult> =
        ResponseEntity.ok(
            authService.login(
                request.email,
                request.password,
            ),
        )
}
