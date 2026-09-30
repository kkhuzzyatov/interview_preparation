package com.backend.desk.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class UpdateDeskRequest(
    @field:NotBlank
    @field:Size(max = 64)
    val name: String,
    @field:NotNull
    val topicId: UUID,
)
