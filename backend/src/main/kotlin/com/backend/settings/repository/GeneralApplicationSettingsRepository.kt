package com.backend.settings.repository

import com.backend.settings.entity.GeneralApplicationSettings
import org.springframework.data.jpa.repository.JpaRepository

interface GeneralApplicationSettingsRepository : JpaRepository<GeneralApplicationSettings, Long>
