package com.backend.user.controller.dto

data class LoginRequest(
    val email: String,
    val password: String,
)
