package com.backend.settings.controller.dto.response

data class RecencyMultiplierResponseDto(
    val recencyMultiplierId: Long,
    val secondsBorder: Int,
    val multiplier: Double,
)
