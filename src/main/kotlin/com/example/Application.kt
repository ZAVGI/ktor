package com.example

import com.example.plugins.configureDatabases
import com.example.plugins.configureDependencyInjection
import com.example.plugins.configureHTTP
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDependencyInjection()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureDatabases()
    configureSecurity()
    configureRouting()
}
