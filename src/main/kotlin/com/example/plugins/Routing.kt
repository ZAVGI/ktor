package com.example.plugins

import com.example.routing.registerAuthRoutes
import com.example.routing.registerEventRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        registerAuthRoutes()
        registerEventRoutes()
    }
}
