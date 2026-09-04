package com.interviewpreparation.feature.desk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun deskScreen(
    deskId: String,
    onBack: () -> Unit,
) {
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

        Text(
            text = "Desk",
        )

        Text(
            text = "Desk ID: $deskId",
        )

        // Desk details/cards will be loaded by DeskViewModel.
    }
}
