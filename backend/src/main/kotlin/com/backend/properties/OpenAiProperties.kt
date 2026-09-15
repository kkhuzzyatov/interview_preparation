package com.backend.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openai")
data class OpenAiProperties(
    val baseUrl: String,
    val apiKey: String,
    val model: String,
)
