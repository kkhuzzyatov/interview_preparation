package com.firstandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firstandroidapp.ui.theme.FirstAndroidAppTheme
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val desks = listOf(
            Desk(
                name = "NLP",
                cardsNew = 20,
                cardsForgotten = 1,
                cardsRepeat = 1,
            ),
            Desk(
                name = "SWE Interviews",
                cardsNew = 20,
                cardsForgotten = 3,
                cardsRepeat = 0,
            ),
            Desk(
                name = "Machine Learning",
                cardsNew = 0,
                cardsForgotten = 0,
                cardsRepeat = 85,
            ),
            Desk(
                name = "Design Patterns",
                cardsNew = 20,
                cardsForgotten = 2,
                cardsRepeat = 0,
            ),
        )

        enableEdgeToEdge()
        setContent {
            FirstAndroidAppTheme {
                if (desks.isEmpty()) {
                    EmptyCollectionImage()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        desks.forEach { desk ->
                            DeskView(desk)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCollectionImage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_box),
            contentDescription = "Image of empty box",
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )

        Text(
            text = "Collection is empty",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DeskView(desk: Desk) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, CardActivity::class.java).apply {
                    putExtra("cardsNew", desk.cardsNew)
                    putExtra("cardsForgotten", desk.cardsForgotten)
                    putExtra("cardsRepeat", desk.cardsRepeat)
                }

                context.startActivity(intent)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = desk.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = desk.cardsNew.toString(),
            fontSize = 12.sp,
            color = Color.Blue,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = desk.cardsForgotten.toString(),
            fontSize = 12.sp,
            color = Color.Red,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = desk.cardsRepeat.toString(),
            fontSize = 12.sp,
            color = Color.Green,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}