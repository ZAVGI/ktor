package com.example

import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApplicationTest {
    @Test
    fun `register login and create event flow works`() = testApplication {
        application { module() }

        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"alice","password":"pass1234"}""")
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"alice","password":"pass1234"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val token = Json.parseToJsonElement(loginResponse.bodyAsText()).jsonObject["token"]?.jsonPrimitive?.content
        assertNotNull(token)

        val createResponse = client.post("/events") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(
                """{"title":"Kotlin Meetup","description":"City event","startsAt":"2026-01-01T10:00:00"}""",
            )
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        assertNotNull(createResponse.headers["X-Response-Time-Ms"])
    }

    @Test
    fun `events endpoint is protected by jwt`() = testApplication {
        application { module() }

        val response = client.get("/events") {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
