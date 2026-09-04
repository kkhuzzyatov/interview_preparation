package com.interviewpreparation.feature.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.interviewpreparation.data.model.AnswerRequest
import com.interviewpreparation.data.model.AnswerResponse
import com.interviewpreparation.data.model.ReviewCardResponse
import com.interviewpreparation.data.remote.InterviewApi
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.UUID

@Composable
fun reviewScreen(
    api: InterviewApi,
    onBack: () -> Unit,
) {
    var answer by remember { mutableStateOf("") }
    var reviewCard by remember { mutableStateOf<ReviewCardResponse?>(null) }
    var answerResponse by remember { mutableStateOf<AnswerResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isLoadingNext by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    suspend fun loadNextQuestion() {
        isLoading = true
        error = null
        answer = ""
        answerResponse = null

        try {
            val response: Response<ReviewCardResponse> =
                api.getNextReviewCard()

            if (response.isSuccessful) {
                reviewCard = response.body()

                if (reviewCard == null) {
                    error = "No review cards available."
                }
            } else {
                reviewCard = null
                error = "Failed to load review card (${response.code()})"
            }
        } catch (e: Exception) {
            reviewCard = null
            error = e.message ?: "Failed to load review card"
        } finally {
            isLoading = false
        }
    }

    fun submitAnswer() {
        val card = reviewCard ?: return

        val cardId = try {
            UUID.fromString(card.cardId)
        } catch (e: IllegalArgumentException) {
            error = "Invalid review card ID"
            return
        }

        isSubmitting = true
        error = null

        scope.launch {
            try {
                val response: Response<AnswerResponse> =
                    api.answer(
                        cardId = cardId,
                        request = AnswerRequest(
                            answer = answer,
                        ),
                    )

                if (response.isSuccessful) {
                    val result = response.body()

                    if (result != null) {
                        answerResponse = result
                    } else {
                        error = "Empty response from server"
                    }
                } else {
                    error = "Failed to submit answer (${response.code()})"
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to submit answer"
            } finally {
                isSubmitting = false
            }
        }
    }

    fun loadNext() {
        scope.launch {
            isLoadingNext = true

            try {
                loadNextQuestion()
            } finally {
                isLoadingNext = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadNextQuestion()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Button(
                onClick = onBack,
            ) {
                Text("BACK")
            }

            Spacer(
                modifier = Modifier.height(24.dp),
            )

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    ErrorContent(
                        message = error!!,
                        onRetry = {
                            scope.launch {
                                loadNextQuestion()
                            }
                        },
                    )
                }

                reviewCard != null -> {
                    ReviewContent(
                        reviewCard = reviewCard!!,
                        answer = answer,
                        answerResponse = answerResponse,
                        isSubmitting = isSubmitting,
                        onAnswerChange = { answer = it },
                        onSubmit = ::submitAnswer,
                    )
                }

                else -> {
                    Text(
                        text = "No review cards available.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        if (answerResponse != null) {
            Button(
                onClick = ::loadNext,
                enabled = !isLoadingNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp,
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                ),
            ) {
                if (isLoadingNext) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Next question")
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        Button(
            onClick = onRetry,
        ) {
            Text("Try again")
        }
    }
}