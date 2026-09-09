package com.backend.review.controller.dto

import java.util.UUID

data class NextCardRequest(
    val deskIds: List<UUID>,
)
