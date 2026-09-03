package com.backend.desk.controller.dto

import java.util.UUID

data class DeskResponse(
    val id: UUID,
    val name: String,
)
