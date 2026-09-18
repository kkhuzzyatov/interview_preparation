package com.backend.settings.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "general_application_settings")
class GeneralApplicationSettings(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_application_setting_id")
    var generalApplicationSettingId: Long,
    @Column(name = "answer_evaluation_prompt", nullable = false, columnDefinition = "TEXT")
    var answerEvaluationPrompt: String,
)
