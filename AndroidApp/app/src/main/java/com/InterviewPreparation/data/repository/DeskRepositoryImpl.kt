package com.interviewpreparation.data.repository

import com.interviewpreparation.data.remote.InterviewApi
import com.interviewpreparation.domain.model.Desk
import com.interviewpreparation.domain.model.DeskStatistics
import com.interviewpreparation.domain.model.DeskWithCards
import com.interviewpreparation.domain.repository.DeskRepository
import java.util.UUID

class DeskRepositoryImpl(
    private val api: InterviewApi,
) : DeskRepository {
    override suspend fun getDesks(): List<Desk> = api.getDesks().map { it.toDomain() }

    override suspend fun getDesk(deskId: UUID): DeskWithCards = api.getDesk(deskId).toDomain()

    override suspend fun getStatistics(): List<DeskStatistics> = api.getStatistics().map { it.toDomain() }
}
