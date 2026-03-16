package com.example.repositories

import com.example.models.EventEntity
import java.time.LocalDateTime

interface EventRepository {
    suspend fun createEvent(ownerId: Int, title: String, description: String, startsAt: LocalDateTime): EventEntity
    suspend fun updateEvent(id: Int, ownerId: Int, title: String, description: String, startsAt: LocalDateTime): EventEntity?
    suspend fun listByOwner(ownerId: Int): List<EventEntity>
}
