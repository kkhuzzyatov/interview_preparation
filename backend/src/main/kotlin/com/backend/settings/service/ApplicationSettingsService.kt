package com.backend.settings.service

import com.backend.settings.controller.dto.SettingsRequestDto
import com.backend.settings.repository.ApplicationSettingsRepository
import com.backend.settings.repository.entity.ApplicationSettings
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationSettingsService(
    private val applicationSettingsRepository: ApplicationSettingsRepository,
) {
    fun get(): ApplicationSettings =
        applicationSettingsRepository
            .findById(SETTINGS_ID)
            .orElseThrow {
                IllegalStateException("Application settings not found")
            }

    @Transactional
    fun update(request: SettingsRequestDto): ApplicationSettings {
        val settings = get()

        settings.answerEvaluationPrompt = request.answerEvaluationPrompt
        settings.evaluationRedAverageScore = request.evaluationRedAverageScore
        settings.evaluationGreenMinAnswers = request.evaluationGreenMinAnswers
        settings.evaluationGreenAverageScore = request.evaluationGreenAverageScore
        settings.reviewMultiplierRecencyDefaultMultiplier =
            request.reviewMultiplierRecencyDefaultMultiplier
        settings.reviewMultiplierMeetChanceMin =
            request.reviewMultiplierMeetChanceMin
        settings.reviewMultiplierMeetChanceMax =
            request.reviewMultiplierMeetChanceMax
        settings.reviewMultiplierMinScore =
            request.reviewMultiplierMinScore
        settings.reviewMultiplierMaxScore =
            request.reviewMultiplierMaxScore
        settings.reviewMultiplierBaseDifficultyMultiplier =
            request.reviewMultiplierBaseDifficultyMultiplier
        settings.reviewMultiplierDefaultDifficultyMultiplier =
            request.reviewMultiplierDefaultDifficultyMultiplier

        return applicationSettingsRepository.save(settings)
    }

    @Transactional
    fun create(request: SettingsRequestDto): ApplicationSettings {
        require(!applicationSettingsRepository.existsById(SETTINGS_ID)) {
            "Application settings already exist"
        }

        val settings =
            ApplicationSettings(
                id = SETTINGS_ID,
                answerEvaluationPrompt = request.answerEvaluationPrompt,
                evaluationRedAverageScore = request.evaluationRedAverageScore,
                evaluationGreenMinAnswers = request.evaluationGreenMinAnswers,
                evaluationGreenAverageScore = request.evaluationGreenAverageScore,
                reviewMultiplierRecencyDefaultMultiplier =
                    request.reviewMultiplierRecencyDefaultMultiplier,
                reviewMultiplierMeetChanceMin =
                    request.reviewMultiplierMeetChanceMin,
                reviewMultiplierMeetChanceMax =
                    request.reviewMultiplierMeetChanceMax,
                reviewMultiplierMinScore =
                    request.reviewMultiplierMinScore,
                reviewMultiplierMaxScore =
                    request.reviewMultiplierMaxScore,
                reviewMultiplierBaseDifficultyMultiplier =
                    request.reviewMultiplierBaseDifficultyMultiplier,
                reviewMultiplierDefaultDifficultyMultiplier =
                    request.reviewMultiplierDefaultDifficultyMultiplier,
            )

        return applicationSettingsRepository.save(settings)
    }

    @Transactional
    fun delete() {
        applicationSettingsRepository.deleteById(SETTINGS_ID)
    }

    private companion object {
        const val SETTINGS_ID = 1L
    }
}
