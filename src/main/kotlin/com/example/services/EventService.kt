package com.example.services

import com.example.models.EventEntity
import com.example.repositories.EventRepository
import java.time.LocalDateTime

class EventService(
    private val eventRepository: EventRepository,
) {
    suspend fun create(ownerId: Int, title: String, description: String, startsAt: String): EventEntity {
        validateEventPayload(title, description)
        val startsAtDate = parseDate(startsAt)
        return eventRepository.createEvent(ownerId, title, description, startsAtDate)
    }

    suspend fun update(id: Int, ownerId: Int, title: String, description: String, startsAt: String): EventEntity? {
        validateEventPayload(title, description)
        val startsAtDate = parseDate(startsAt)
        return eventRepository.updateEvent(id, ownerId, title, description, startsAtDate)
    }

    suspend fun listByOwner(ownerId: Int): List<EventEntity> = eventRepository.listByOwner(ownerId)

    private fun validateEventPayload(title: String, description: String) {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(description.length in 3..1024) { "Description length must be 3..1024" }
    }

    private fun parseDate(startsAt: String): LocalDateTime =
        runCatching { LocalDateTime.parse(startsAt) }
            .getOrElse { throw IllegalArgumentException("startsAt should be in ISO-8601 LocalDateTime format") }
}
