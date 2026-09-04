package com.interviewpreparation.data.model

import com.interviewpreparation.domain.model.DeskStatistics
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeskStatisticsResponse(
    val deskId: String,
    val deskName: String,
    val blue: Int,
    val red: Int,
    val yellow: Int,
    val green: Int,
) {
    fun toDomain(): DeskStatistics =
        DeskStatistics(
            deskId = UUID.fromString(deskId),
            deskName = deskName,
            blue = blue,
            red = red,
            yellow = yellow,
            green = green,
        )
}
