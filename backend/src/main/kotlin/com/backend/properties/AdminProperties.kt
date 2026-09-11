package com.backend.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "admin")
data class AdminProperties(
    val cardsImprovementKey: String,
)
