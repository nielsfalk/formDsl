package de.nielsfalk.form_dsl.server

import de.nielsfalk.form_dsl.server.plugins.AllFormsResponse
import de.nielsfalk.form_dsl.server.plugins.configureRouting
import de.nielsfalk.form_dsl.server.plugins.configureSerialization
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*

class GetAllFormsTest : StringSpec({


    "GET /forms" {
        testApplication {
            application {
                configureSerialization()
                configureRouting()
            }

            httpClient()
                .get("/forms").apply {

                    status shouldBe OK
                    body<AllFormsResponse>().forms should containOnly("foo", "bar")
                }
        }
    }
})

private fun ApplicationTestBuilder.httpClient() =
    createClient {
        install(ContentNegotiation) {
            json()
        }
    }
