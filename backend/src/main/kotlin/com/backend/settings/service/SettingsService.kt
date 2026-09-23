package com.backend.settings.service

import com.backend.settings.controller.dto.request.SettingsRequestDto
import com.backend.settings.controller.dto.response.DifficultyMultiplierResponseDto
import com.backend.settings.controller.dto.response.MeetChanceMultiplierResponseDto
import com.backend.settings.controller.dto.response.RecencyMultiplierResponseDto
import com.backend.settings.controller.dto.response.ScoreColorResponseDto
import com.backend.settings.controller.dto.response.SettingsResponseDto
import com.backend.settings.entity.DifficultyMultiplier
import com.backend.settings.entity.GeneralApplicationSettings
import com.backend.settings.entity.MeetChanceMultiplier
import com.backend.settings.entity.RecencyMultiplier
import com.backend.settings.entity.ScoreColor
import com.backend.settings.repository.DifficultyMultiplierRepository
import com.backend.settings.repository.GeneralApplicationSettingsRepository
import com.backend.settings.repository.MeetChanceMultiplierRepository
import com.backend.settings.repository.RecencyMultiplierRepository
import com.backend.settings.repository.ScoreColorRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val generalApplicationSettingsRepository: GeneralApplicationSettingsRepository,
    private val difficultyMultiplierRepository: DifficultyMultiplierRepository,
    private val meetChanceMultiplierRepository: MeetChanceMultiplierRepository,
    private val recencyMultiplierRepository: RecencyMultiplierRepository,
    private val scoreColorRepository: ScoreColorRepository,
) {
    fun get(): SettingsResponseDto {
        val applicationSettings =
            generalApplicationSettingsRepository
                .findAll()
                .first()

        return SettingsResponseDto(
            answerEvaluationPrompt = applicationSettings.answerEvaluationPrompt,
            newCardRecencyMultiplier = applicationSettings.newCardRecencyMultiplier,
            newCardDifficultyMultiplier = applicationSettings.newCardDifficultyMultiplier,
            newCardColor = applicationSettings.newCardColor,
            difficultyMultipliers =
                difficultyMultiplierRepository
                    .findAll()
                    .map {
                        DifficultyMultiplierResponseDto(
                            difficultyMultiplierId = it.difficultyMultiplierId!!,
                            lastAnswerScoreBorder = it.lastAnswerScoreBorder,
                            multiplier = it.multiplier,
                        )
                    },
            meetChanceMultipliers =
                meetChanceMultiplierRepository
                    .findAll()
                    .map {
                        MeetChanceMultiplierResponseDto(
                            meetChanceMultiplierId = it.meetChanceMultiplierId!!,
                            meetChanceBorder = it.meetChanceBorder,
                            multiplier = it.multiplier,
                        )
                    },
            recencyMultipliers =
                recencyMultiplierRepository
                    .findAll()
                    .map {
                        RecencyMultiplierResponseDto(
                            recencyMultiplierId = it.recencyMultiplierId!!,
                            secondsBorder = it.secondsBorder,
                            multiplier = it.multiplier,
                        )
                    },
            scoreColor =
                scoreColorRepository
                    .findAll()
                    .map {
                        ScoreColorResponseDto(
                            scoreColorsId = it.scoreColorsId!!,
                            score = it.score,
                            colorHex = it.colorHex,
                        )
                    },
        )
    }

    @Transactional
    fun update(request: SettingsRequestDto) {
        generalApplicationSettingsRepository.deleteAll()
        difficultyMultiplierRepository.deleteAll()
        meetChanceMultiplierRepository.deleteAll()
        recencyMultiplierRepository.deleteAll()
        scoreColorRepository.deleteAll()

        generalApplicationSettingsRepository.save(
            GeneralApplicationSettings(
                generalApplicationSettingId = null,
                answerEvaluationPrompt = request.answerEvaluationPrompt,
                newCardRecencyMultiplier = request.newCardRecencyMultiplier,
                newCardDifficultyMultiplier = request.newCardDifficultyMultiplier,
                newCardColor = request.newCardColor,
            ),
        )

        difficultyMultiplierRepository.saveAll(
            request.difficultyMultipliers.map {
                DifficultyMultiplier(
                    difficultyMultiplierId = null,
                    lastAnswerScoreBorder = it.lastAnswerScoreBorder,
                    multiplier = it.multiplier,
                )
            },
        )

        meetChanceMultiplierRepository.saveAll(
            request.meetChanceMultipliers.map {
                MeetChanceMultiplier(
                    meetChanceMultiplierId = null,
                    meetChanceBorder = it.meetChanceBorder,
                    multiplier = it.multiplier,
                )
            },
        )

        recencyMultiplierRepository.saveAll(
            request.recencyMultipliers.map {
                RecencyMultiplier(
                    recencyMultiplierId = null,
                    secondsBorder = it.secondsBorder,
                    multiplier = it.multiplier,
                )
            },
        )

        scoreColorRepository.saveAll(
            request.scoreColor.map {
                ScoreColor(
                    scoreColorsId = null,
                    score = it.score,
                    colorHex = it.colorHex,
                )
            },
        )
    }
}
