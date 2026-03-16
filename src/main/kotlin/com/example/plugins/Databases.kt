package com.example.plugins

import com.example.models.EventsTable
import com.example.models.UsersTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val url = environment.config.property("database.url").getString()
    val driver = environment.config.property("database.driver").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()

    Database.connect(url = url, driver = driver, user = user, password = password)

    transaction {
        SchemaUtils.create(UsersTable, EventsTable)
    }
}
