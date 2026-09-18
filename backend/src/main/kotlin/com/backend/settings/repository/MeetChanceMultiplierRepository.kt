package com.backend.settings.repository

import com.backend.settings.entity.MeetChanceMultiplier
import org.springframework.data.jpa.repository.JpaRepository

interface MeetChanceMultiplierRepository : JpaRepository<MeetChanceMultiplier, Long>
