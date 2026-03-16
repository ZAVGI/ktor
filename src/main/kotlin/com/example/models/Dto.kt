package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val token: String,
)

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String,
    val startsAt: String,
)

@Serializable
data class UpdateEventRequest(
    val title: String,
    val description: String,
    val startsAt: String,
)

@Serializable
data class EventResponse(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val description: String,
    val startsAt: String,
)

@Serializable
data class ErrorResponse(
    val message: String,
)
