package com.backend.desk.controller.dto

data class DeskDto(
    val name: String,
    val cardsNew: Int,
    val cardsForgotten: Int,
    val cardsRepeat: Int
)