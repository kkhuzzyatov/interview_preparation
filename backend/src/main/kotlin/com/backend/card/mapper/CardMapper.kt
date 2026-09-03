package com.backend.card.mapper

import com.backend.card.controller.dto.CardResponse
import com.backend.card.entity.Card

object CardMapper {
    fun toResponse(entity: Card): CardResponse =
        CardResponse(
            id = entity.id,
            question = entity.question,
            answer = entity.answer,
            meetChance = entity.meetChance,
        )
}
