package com.backend.config

import com.backend.client.OpenAiApiClient
import com.backend.properties.OpenAiProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class OpenAiClientConfig {
    @Bean
    fun openAiApiClient(
        restClient: RestClient,
        properties: OpenAiProperties,
    ): OpenAiApiClient =
        OpenAiApiClient(
            restClient = restClient,
            properties = properties,
        )
}
