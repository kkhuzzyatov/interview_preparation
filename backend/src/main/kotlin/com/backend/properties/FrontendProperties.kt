package com.backend.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.frontend")
data class FrontendProperties(
    val url: String,
)
