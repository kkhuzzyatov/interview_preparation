package com.backend.desk.repository

import com.backend.desk.entity.Desk
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeskRepository : JpaRepository<Desk, UUID>
