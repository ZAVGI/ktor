package com.example.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 64).uniqueIndex()
    val password = varchar("password", 128)

    override val primaryKey = PrimaryKey(id)
}

object EventsTable : Table("events") {
    val id = integer("id").autoIncrement()
    val ownerId = integer("owner_id").references(UsersTable.id)
    val title = varchar("title", 128)
    val description = varchar("description", 1024)
    val startsAt = datetime("starts_at")

    override val primaryKey = PrimaryKey(id)
}

data class UserEntity(
    val id: Int,
    val username: String,
    val password: String,
)

data class EventEntity(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val description: String,
    val startsAt: LocalDateTime,
)

fun ResultRow.toUserEntity() = UserEntity(
    id = this[UsersTable.id],
    username = this[UsersTable.username],
    password = this[UsersTable.password],
)

fun ResultRow.toEventEntity() = EventEntity(
    id = this[EventsTable.id],
    ownerId = this[EventsTable.ownerId],
    title = this[EventsTable.title],
    description = this[EventsTable.description],
    startsAt = this[EventsTable.startsAt],
)
