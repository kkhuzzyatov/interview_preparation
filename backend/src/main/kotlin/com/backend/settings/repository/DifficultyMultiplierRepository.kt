package com.backend.settings.repository

import com.backend.settings.entity.DifficultyMultiplier
import org.springframework.data.jpa.repository.JpaRepository

interface DifficultyMultiplierRepository : JpaRepository<DifficultyMultiplier, Long>
