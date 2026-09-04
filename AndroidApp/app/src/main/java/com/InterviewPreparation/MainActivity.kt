package com.interviewpreparation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.interviewpreparation.navigation.appNavigation
import com.interviewpreparation.ui.theme.interviewPreparationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            interviewPreparationTheme {
                appNavigation()
            }
        }
    }
}
