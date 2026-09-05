package com.backend.user.controller.dto

data class RegisterRequest(
    val email: String,
    val password: String,
)
