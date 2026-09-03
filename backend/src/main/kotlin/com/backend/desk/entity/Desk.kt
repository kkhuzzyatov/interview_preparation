package com.backend.desk.entity

import com.backend.card.entity.Card
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "desks")
class Desk(
    @Id
    @Column(name = "desk_id", nullable = false)
    val id: UUID,
    @Column(name = "name", nullable = false, length = 64)
    var name: String,
    @OneToMany(
        mappedBy = "desk",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    val cards: MutableList<Card> = mutableListOf(),
)
