package com.backend.user.controller.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)
