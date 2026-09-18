package com.backend.settings.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "score_color")
class ScoreColor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_colors_id")
    var scoreColorsId: Long? = null,
    @Column(name = "score", nullable = false)
    var score: Int,
    @Column(name = "color_hex", nullable = false)
    var colorHex: String,
)
