package com.backend.settings.repository

import com.backend.settings.entity.ScoreColor
import org.springframework.data.jpa.repository.JpaRepository

interface ScoreColorRepository : JpaRepository<ScoreColor, Long>
