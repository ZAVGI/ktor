package com.example.plugins

import com.example.repositories.EventRepository
import com.example.repositories.ExposedEventRepository
import com.example.repositories.ExposedUserRepository
import com.example.repositories.UserRepository
import com.example.services.AuthService
import com.example.services.EventService
import com.example.services.JwtConfig
import io.ktor.server.application.Application
import io.insert-koin.ktor.plugin.Koin
import org.koin.dsl.module

fun Application.configureDependencyInjection() {
    val jwtConfig = JwtConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString(),
        secret = environment.config.property("jwt.secret").getString(),
    )

    install(Koin) {
        modules(
            module {
                single { jwtConfig }
                single<UserRepository> { ExposedUserRepository() }
                single<EventRepository> { ExposedEventRepository() }
                single { AuthService(get(), get()) }
                single { EventService(get()) }
            },
        )
    }
}
