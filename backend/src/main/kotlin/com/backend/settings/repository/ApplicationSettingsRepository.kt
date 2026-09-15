package com.backend.settings.repository

import com.backend.settings.repository.entity.ApplicationSettings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationSettingsRepository : JpaRepository<ApplicationSettings, Long> {
    fun findFirstByOrderById(): ApplicationSettings?
}
