package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.UserEntity
import com.example.repositories.UserRepository

class AuthService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig,
) {
    suspend fun register(username: String, password: String): UserEntity {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(password.length >= 4) { "Password must contain at least 4 characters" }

        val existingUser = userRepository.findByUsername(username)
        require(existingUser == null) { "User already exists" }

        return userRepository.createUser(username = username, password = password)
    }

    suspend fun login(username: String, password: String): String {
        val user = userRepository.findByUsername(username) ?: error("Invalid credentials")
        require(user.password == password) { "Invalid credentials" }

        return JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withClaim("userId", user.id)
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }
}

data class JwtConfig(
    val issuer: String,
    val audience: String,
    val realm: String,
    val secret: String,
)
