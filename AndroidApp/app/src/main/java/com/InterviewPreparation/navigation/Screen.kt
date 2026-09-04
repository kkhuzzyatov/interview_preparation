package com.interviewpreparation.navigation

sealed interface Screen {
    data object Home : Screen

    data object Review : Screen

    data class Desk(
        val deskId: String,
    ) : Screen
}
