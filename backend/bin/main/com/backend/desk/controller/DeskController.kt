package com.backend.desk.controller

import com.backend.desk.controller.dto.DeskDto
import com.backend.desk.controller.dto.DeskResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DeskController {

    @GetMapping("/api/desks")
    fun getDesks(): DeskResponseDto {
        return DeskResponseDto(
            desks = listOf(
                DeskDto(
                    name = "NLP",
                    cardsNew = 20,
                    cardsForgotten = 1,
                    cardsRepeat = 1
                ),
                DeskDto(
                    name = "SWE Interviews",
                    cardsNew = 20,
                    cardsForgotten = 3,
                    cardsRepeat = 0
                ),
                DeskDto(
                    name = "Machine Learning",
                    cardsNew = 0,
                    cardsForgotten = 0,
                    cardsRepeat = 85
                ),
                DeskDto(
                    name = "Design Patterns",
                    cardsNew = 20,
                    cardsForgotten = 2,
                    cardsRepeat = 0
                )
            )
        )
    }
}