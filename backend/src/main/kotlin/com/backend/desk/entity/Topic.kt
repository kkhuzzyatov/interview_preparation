package com.backend.desk.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "topics")
class Topic(
    @Id
    @Column(name = "topic_id", nullable = false)
    val id: UUID,
    @Column(name = "name", nullable = false, length = 64, unique = true)
    var name: String,
)
