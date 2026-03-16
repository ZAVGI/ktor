package com.example.repositories

import com.example.models.UserEntity
import com.example.models.UsersTable
import com.example.models.toUserEntity
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ExposedUserRepository : UserRepository {
    override suspend fun createUser(username: String, password: String): UserEntity = newSuspendedTransaction(Dispatchers.IO) {
        val id = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.password] = password
        }[UsersTable.id]

        UserEntity(id = id, username = username, password = password)
    }

    override suspend fun findByUsername(username: String): UserEntity? = newSuspendedTransaction(Dispatchers.IO) {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .map { it.toUserEntity() }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): UserEntity? = newSuspendedTransaction(Dispatchers.IO) {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUserEntity() }
            .singleOrNull()
    }
}
