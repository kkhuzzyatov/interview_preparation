package com.interviewpreparation.data.model

import com.interviewpreparation.domain.model.Desk
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeskResponse(
    val id: String,
    val name: String,
) {
    fun toDomain(): Desk =
        Desk(
            id = UUID.fromString(id),
            name = name,
        )
}
