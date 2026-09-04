package com.interviewpreparation.feature.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.interviewpreparation.data.model.AnswerResponse
import com.interviewpreparation.data.model.ReviewCardResponse

@Composable
fun ReviewContent(
    reviewCard: ReviewCardResponse,
    answer: String,
    answerResponse: AnswerResponse?,
    isSubmitting: Boolean,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = reviewCard.deskName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(
            modifier = Modifier.height(20.dp),
        )

        Text(
            text = reviewCard.question,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(
            modifier = Modifier.height(24.dp),
        )

        if (answerResponse == null) {
            Text(
                text = "Your Answer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(
                modifier = Modifier.height(8.dp),
            )

            OutlinedTextField(
                value = answer,
                onValueChange = onAnswerChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Your answer")
                },
                minLines = 6,
                enabled = !isSubmitting,
            )

            Spacer(
                modifier = Modifier.height(16.dp),
            )

            Button(
                onClick = onSubmit,
                enabled = answer.isNotBlank() && !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("SUBMIT")
                }
            }
        } else {
            ReviewResult(
                answer = answer,
                response = answerResponse,
            )
        }
    }
}

@Composable
private fun ReviewResult(
    answer: String,
    response: AnswerResponse,
) {
    ReviewSection(
        title = "Your Answer",
        content = answer,
    )

    Spacer(
        modifier = Modifier.height(20.dp),
    )

    ReviewSection(
        title = "Score",
        content = response.score.toString(),
    )

    Spacer(
        modifier = Modifier.height(20.dp),
    )

    ReviewSection(
        title = "Feedback",
        content = response.feedback,
    )

    Spacer(
        modifier = Modifier.height(20.dp),
    )

    ReviewSection(
        title = "Correct Answer",
        content = response.correctAnswer,
    )
}

@Composable
private fun ReviewSection(
    title: String,
    content: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        Text(
            text = content,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}