package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.response.header
import io.ktor.util.AttributeKey

val ResponseTimerPlugin = createApplicationPlugin(name = "ResponseTimerPlugin") {
    val startKey = AttributeKey<Long>("requestStartTime")

    onCall { call ->
        call.attributes.put(startKey, System.nanoTime())
    }

    onCallRespond { call, _ ->
        val elapsedMs = (System.nanoTime() - call.attributes[startKey]) / 1_000_000
        call.response.header("X-Response-Time-Ms", elapsedMs.toString())
    }
}

fun Application.configureHTTP() {
    install(ResponseTimerPlugin)
}
