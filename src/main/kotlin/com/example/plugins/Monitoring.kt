package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.request.receiveText
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(DoubleReceive)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val body = runCatching { call.receiveText() }.getOrElse { "<no-body>" }
            "${call.request.httpMethod.value} ${call.request.uri} body=$body"
        }
    }
}
