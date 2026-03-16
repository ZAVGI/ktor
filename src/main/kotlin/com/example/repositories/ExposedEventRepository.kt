package com.example.repositories

import com.example.models.EventEntity
import com.example.models.EventsTable
import com.example.models.toEventEntity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ExposedEventRepository : EventRepository {
    override suspend fun createEvent(ownerId: Int, title: String, description: String, startsAt: LocalDateTime): EventEntity =
        newSuspendedTransaction(Dispatchers.IO) {
            val id = EventsTable.insert {
                it[EventsTable.ownerId] = ownerId
                it[EventsTable.title] = title
                it[EventsTable.description] = description
                it[EventsTable.startsAt] = startsAt
            }[EventsTable.id]

            EventEntity(id = id, ownerId = ownerId, title = title, description = description, startsAt = startsAt)
        }

    override suspend fun updateEvent(
        id: Int,
        ownerId: Int,
        title: String,
        description: String,
        startsAt: LocalDateTime,
    ): EventEntity? = newSuspendedTransaction(Dispatchers.IO) {
        val updatedRows = EventsTable.update({ (EventsTable.id eq id) and (EventsTable.ownerId eq ownerId) }) {
            it[EventsTable.title] = title
            it[EventsTable.description] = description
            it[EventsTable.startsAt] = startsAt
        }

        if (updatedRows == 0) return@newSuspendedTransaction null

        EventsTable.selectAll()
            .where { EventsTable.id eq id }
            .map { it.toEventEntity() }
            .singleOrNull()
    }

    override suspend fun listByOwner(ownerId: Int): List<EventEntity> = newSuspendedTransaction(Dispatchers.IO) {
        EventsTable.selectAll()
            .where { EventsTable.ownerId eq ownerId }
            .map { it.toEventEntity() }
    }
}
