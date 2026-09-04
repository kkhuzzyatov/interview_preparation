package com.interviewpreparation.domain.model

import java.util.UUID

data class DeskStatistics(
    val deskId: UUID,
    val deskName: String,
    val blue: Int,
    val red: Int,
    val yellow: Int,
    val green: Int,
) {
    val total: Int
        get() = blue + red + yellow + green
}
