package com.backend.answer.entity

import com.backend.card.entity.Card
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "answers")
class Answer(
    @Id
    @Column(name = "answer_id", nullable = false)
    val id: UUID,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "card_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_answers_card"),
    )
    var card: Card,
    @Column(name = "score", nullable = false)
    var score: Int,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
)
