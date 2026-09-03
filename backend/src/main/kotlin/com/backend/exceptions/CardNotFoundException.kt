package com.backend.exceptions

import java.util.UUID

class CardNotFoundException(
    cardId: UUID,
) : RuntimeException(
        "Card not found: $cardId",
    )
