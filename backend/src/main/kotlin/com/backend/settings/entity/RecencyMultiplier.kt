package com.backend.settings.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "recency_multipliers")
class RecencyMultiplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recency_multiplier_id")
    var recencyMultiplierId: Long?,
    @Column(name = "seconds_border", nullable = false)
    var secondsBorder: Int,
    @Column(name = "multiplier", nullable = false)
    var multiplier: Double,
)
