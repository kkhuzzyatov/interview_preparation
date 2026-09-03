package com.backend.card.repository

import com.backend.card.entity.Card
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CardRepository : JpaRepository<Card, UUID> {
    fun findByDeskId(deskId: UUID): List<Card>
}
