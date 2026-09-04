package com.interviewpreparation.domain.repository

import com.interviewpreparation.domain.model.Desk
import com.interviewpreparation.domain.model.DeskStatistics
import com.interviewpreparation.domain.model.DeskWithCards
import java.util.UUID

interface DeskRepository {
    suspend fun getDesks(): List<Desk>

    suspend fun getDesk(deskId: UUID): DeskWithCards

    suspend fun getStatistics(): List<DeskStatistics>
}
