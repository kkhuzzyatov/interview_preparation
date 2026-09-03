package com.backend.card.entity

import com.backend.desk.entity.Desk
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "cards")
class Card(

    @Id
    @Column(name = "card_id", nullable = false)
    val id: UUID,

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    var question: String,

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    var answer: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "desk_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_cards_desk")
    )
    var desk: Desk,

    @Column(
        name = "meet_chance",
        nullable = false,
        precision = 10,
        scale = 2
    )
    var meetChance: BigDecimal
)