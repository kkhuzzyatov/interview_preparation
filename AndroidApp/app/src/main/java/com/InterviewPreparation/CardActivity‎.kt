package com.interviewpreparation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cardsNew = intent.getIntExtra("cardsNew", 0)
        val cardsForgotten = intent.getIntExtra("cardsForgotten", 0)
        val cardsRepeat = intent.getIntExtra("cardsRepeat", 0)

        setContent {
            Header(
                cardsNew = cardsNew,
                cardsForgotten = cardsForgotten,
                cardsRepeat = cardsRepeat,
                onMenuClick = {
                    finish()
                }
            )
        }
    }
}

@Composable
fun Header(
    cardsNew: Int,
    cardsForgotten: Int,
    cardsRepeat: Int,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F8FF))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onMenuClick) {
            Text(
                text = "☰",
                fontSize = 24.sp
            )
        }

        Text(
            text = cardsNew.toString(),
            color = Color(0xFF00A8FF)
        )

        Text(
            text = cardsForgotten.toString(),
            color = Color.Red
        )

        Text(
            text = cardsRepeat.toString(),
            color = Color.Green
        )
    }
}