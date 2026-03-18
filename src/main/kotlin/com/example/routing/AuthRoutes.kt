package com.example.routing

import com.example.models.AuthResponse
import com.example.models.ErrorResponse
import com.example.models.RegisterResponse
import com.example.models.LoginRequest
import com.example.models.RegisterRequest
import com.example.services.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.registerAuthRoutes() {
    val authService by inject<AuthService>()

    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val user = runCatching { authService.register(request.username, request.password) }
                .getOrElse {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(it.message ?: "registration failed"))
                    return@post
                }

            call.respond(HttpStatusCode.Created, RegisterResponse(user.id, user.username))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val token = runCatching { authService.login(request.username, request.password) }
                .getOrElse {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(it.message ?: "login failed"))
                    return@post
                }
            call.respond(AuthResponse(token))
        }
    }
}
