package com.backend.client

import com.backend.properties.OpenAiProperties
import tools.jackson.databind.ObjectMapper
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class OpenAiApiClient(
    private val restClient: RestClient,
    private val properties: OpenAiProperties,
    private val objectMapper: ObjectMapper,
) {
    fun checkAndImproveAnswer(
        question: String,
        answer: String,
    ): ImproveAnswerResult {
        val prompt =
            """
            Ты — опытный senior software engineer, технический интервьюер и редактор технических ответов.
            
            Оцени следующий ответ на технический вопрос.
            
            Вопрос:
            $question
            
            Текущий ответ:
            $answer
            
            Твоя задача:
            1. Определи, является ли текущий ответ достаточно хорошим для технического собеседования.
            2. Если ответ хороший и не требует существенных изменений, установи shouldImprove в false.
            3. Если ответ содержит ошибки, неполный, неясный, устаревший, плохо структурирован или иным образом требует улучшения,
               установи shouldImprove в true и предоставь исправленный, улучшенный ответ.
            4. Если ответ пустой, отсутствует или практически бесполезен, создай ответ с нуля.
            5. Сохрани всю технически корректную информацию из текущего ответа.
            6. При необходимости добавь важные детали, которые могут быть полезны на техническом собеседовании.
            7. Удали ненужные повторения, воду и лишнюю информацию.
            8. Ответ должен быть кратким, но при этом достаточно полным для успешного ответа на техническом собеседовании.
            
            Приложение НЕ поддерживает Markdown.
            
            Разрешено:
            - Обычный текст
            - Абзацы
            - Списки с использованием "-"
            
            Запрещено:
            - Markdown-заголовки
            - **жирный текст**
            - *курсив*
            - Markdown-таблицы
            - Backticks
            - HTML
            - Ссылки
            - Любое другое Markdown-форматирование
            
            Верни ТОЛЬКО валидный JSON строго в следующем формате:
            
            {
              "shouldImprove": true,
              "improvedAnswer": "..."
            }
            
            Если текущий ответ уже хороший:
            
            {
              "shouldImprove": false,
              "improvedAnswer": null
            }
            """.trimIndent()

        val content =
            sendPrompt(
                model = properties.improvementModel,
                prompt = prompt,
            )

        return objectMapper.readValue(
            content,
            ImproveAnswerResult::class.java,
        )
    }

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
