package com.backend.desk.repository

import com.backend.desk.entity.Desk
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface DeskRepository : JpaRepository<Desk, UUID> {
    @EntityGraph(attributePaths = ["cards"])
    override fun findAll(): List<Desk>

    @EntityGraph(attributePaths = ["cards"])
    fun findWithCardsById(id: UUID): Optional<Desk>
}
