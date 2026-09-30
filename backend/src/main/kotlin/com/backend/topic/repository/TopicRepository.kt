package com.backend.topic.repository

import com.backend.topic.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TopicRepository : JpaRepository<Topic, UUID>
