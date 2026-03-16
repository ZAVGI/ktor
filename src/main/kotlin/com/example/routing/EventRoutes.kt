package com.example.routing

import com.example.models.CreateEventRequest
import com.example.models.ErrorResponse
import com.example.models.EventResponse
import com.example.models.UpdateEventRequest
import com.example.services.EventService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.registerEventRoutes() {
    val eventService by inject<EventService>()

    authenticate("auth-jwt") {
        route("/events") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: run {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing userId in token"))
                        return@post
                    }

                val request = call.receive<CreateEventRequest>()
                val event = runCatching {
                    eventService.create(userId, request.title, request.description, request.startsAt)
                }.getOrElse {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(it.message ?: "failed to create event"))
                    return@post
                }

                call.respond(
                    HttpStatusCode.Created,
                    EventResponse(event.id, event.ownerId, event.title, event.description, event.startsAt.toString()),
                )
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: run {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing userId in token"))
                        return@put
                    }
                val eventId = call.parameters["id"]?.toIntOrNull()
                    ?: run {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid event id"))
                        return@put
                    }
                val request = call.receive<UpdateEventRequest>()

                val updated = runCatching {
                    eventService.update(eventId, userId, request.title, request.description, request.startsAt)
                }.getOrElse {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(it.message ?: "failed to update event"))
                    return@put
                }

                if (updated == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Event not found or access denied"))
                    return@put
                }

                call.respond(
                    EventResponse(
                        updated.id,
                        updated.ownerId,
                        updated.title,
                        updated.description,
                        updated.startsAt.toString(),
                    ),
                )
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: run {
                        call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing userId in token"))
                        return@get
                    }

                val events = eventService.listByOwner(userId).map {
                    EventResponse(it.id, it.ownerId, it.title, it.description, it.startsAt.toString())
                }
                call.respond(events)
            }
        }
    }
}
