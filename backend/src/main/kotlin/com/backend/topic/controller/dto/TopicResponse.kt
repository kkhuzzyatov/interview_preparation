package com.backend.topic.controller.dto

import java.util.UUID

data class TopicResponse(
    val id: UUID,
    val name: String,
)
