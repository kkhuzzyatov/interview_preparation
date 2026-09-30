package com.backend.topic.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateTopicRequest(
    @field:NotBlank
    @field:Size(max = 64)
    val name: String,
)
