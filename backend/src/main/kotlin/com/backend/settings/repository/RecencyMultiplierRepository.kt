package com.backend.settings.repository

import com.backend.settings.entity.RecencyMultiplier
import org.springframework.data.jpa.repository.JpaRepository

interface RecencyMultiplierRepository : JpaRepository<RecencyMultiplier, Long>
