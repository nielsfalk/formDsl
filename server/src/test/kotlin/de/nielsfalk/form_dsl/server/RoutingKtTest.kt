package de.nielsfalk.form_dsl.server

import de.nielsfalk.form_dsl.server.plugins.configureSerialization
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.forms.allForms
import de.nielsfalk.formdsl.forms.noodleId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.contain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json

class RoutingKtTest : StringSpec({
    "GET /forms" {
        testApplication {
            application {
                configureSerialization()
                configureRouting()
            }

            httpClient()
                .get("/forms").apply {

                    status shouldBe OK
                    body<AllFormsResponse>().forms should contain("a noodle survey")
                }
        }
    }
    "GET /form/{id}" {
        testApplication {
            application {
                configureSerialization()
                configureRouting()
            }
            val id = noodleId

            httpClient().get("/forms/$id").apply {

                status shouldBe OK
                body<Form>() shouldBe allForms.first { it.id.hexString == id }
            }
        }
    }
})

private fun ApplicationTestBuilder.httpClient() =
    createClient {
        install(ContentNegotiation) {
            json(Json { encodeDefaults = true })
        }
    }
