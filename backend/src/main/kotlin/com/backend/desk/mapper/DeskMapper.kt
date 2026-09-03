package com.backend.desk.mapper

import com.backend.card.mapper.CardMapper
import com.backend.desk.controller.dto.DeskResponse
import com.backend.desk.controller.dto.DeskWithCardsResponse
import com.backend.desk.entity.Desk

object DeskMapper {
    fun toResponse(entity: Desk): DeskResponse =
        DeskResponse(
            id = entity.id,
            name = entity.name,
        )

    fun toResponseWithCards(entity: Desk): DeskWithCardsResponse =
        DeskWithCardsResponse(
            id = entity.id,
            name = entity.name,
            cards =
                entity.cards.map { card ->
                    CardMapper.toResponse(card)
                },
        )
}
