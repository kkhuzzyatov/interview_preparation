package com.interviewpreparation.feature.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun reviewScreen(onBack: () -> Unit) {
    var answer by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Button(
            onClick = onBack,
        ) {
            Text("BACK")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Review",
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Question will appear here",
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Your answer")
            },
            minLines = 5,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Submit answer through ReviewViewModel.
            },
            enabled = answer.isNotBlank(),
        ) {
            Text("SUBMIT")
        }
    }
}
