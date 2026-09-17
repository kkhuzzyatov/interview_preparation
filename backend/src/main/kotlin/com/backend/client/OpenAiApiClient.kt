package com.backend.client

import com.backend.properties.OpenAiProperties
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import tools.jackson.databind.ObjectMapper

class OpenAiApiClient(
    private val restClient: RestClient,
    private val properties: OpenAiProperties,
    private val objectMapper: ObjectMapper,
) {
    fun sendPrompt(prompt: String): String =
        sendPrompt(
            model = properties.model,
            prompt = prompt,
        )

    private fun sendPrompt(
        model: String,
        prompt: String,
    ): String {
        val response =
            restClient
                .post()
                .uri("${properties.baseUrl}/chat/completions")
                .header("Authorization", "Bearer ${properties.apiKey}")
                .header("Content-Type", "application/json")
                .body(
                    ChatCompletionRequest(
                        model = model,
                        messages =
                            listOf(
                                Message(
                                    role = "user",
                                    content = prompt,
                                ),
                            ),
                    ),
                ).retrieve()
                .body<OpenAiResponse>()
                ?: throw IllegalStateException("OpenAI response is empty")

        val choice =
            response.choices
                .firstOrNull()
                ?: throw IllegalStateException(
                    "OpenAI response contains no choices",
                )

        return choice.message.content
    }

    data class ImproveAnswerResult(
        val shouldImprove: Boolean,
        val improvedAnswer: String?,
    )

    private data class ChatCompletionRequest(
        val model: String,
        val messages: List<Message>,
    )

    private data class Message(
        val role: String,
        val content: String,
    )

    private data class OpenAiResponse(
        val choices: List<Choice>,
    )

    private data class Choice(
        val message: Message,
    )
}
