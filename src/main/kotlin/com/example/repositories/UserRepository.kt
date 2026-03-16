package com.example.repositories

import com.example.models.UserEntity

interface UserRepository {
    suspend fun createUser(username: String, password: String): UserEntity
    suspend fun findByUsername(username: String): UserEntity?
    suspend fun findById(id: Int): UserEntity?
}
