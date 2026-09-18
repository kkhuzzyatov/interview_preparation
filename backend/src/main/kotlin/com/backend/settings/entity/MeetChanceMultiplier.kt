package com.backend.settings.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "meet_chance_multipliers")
class MeetChanceMultiplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meet_chance_multiplier_id")
    var meetChanceMultiplierId: Long,
    @Column(name = "meet_chance_border", nullable = false)
    var meetChanceBorder: Double,
    @Column(name = "multiplier", nullable = false)
    var multiplier: Double,
)
