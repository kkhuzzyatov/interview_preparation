package com.backend.card.controller.dto

import java.util.UUID

data class CreateCardRequest(
    val question: String,
    val answer: String,
    val deskId: UUID,
)
