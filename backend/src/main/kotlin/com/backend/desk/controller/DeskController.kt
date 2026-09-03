package com.backend.desk.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DeskController {
    @GetMapping("/api/desks")
    fun getDesks(): DeskResponseDto =
        DeskResponseDto(
            desks =
                listOf(
                    `DeskResponse.kt`(
                        name = "NLP",
                        cardsNew = 20,
                        cardsForgotten = 1,
                        cardsRepeat = 1,
                    ),
                    `DeskResponse.kt`(
                        name = "SWE Interviews",
                        cardsNew = 20,
                        cardsForgotten = 3,
                        cardsRepeat = 0,
                    ),
                    `DeskResponse.kt`(
                        name = "Machine Learning",
                        cardsNew = 0,
                        cardsForgotten = 0,
                        cardsRepeat = 85,
                    ),
                    `DeskResponse.kt`(
                        name = "Design Patterns",
                        cardsNew = 20,
                        cardsForgotten = 2,
                        cardsRepeat = 0,
                    ),
                ),
        )
}
