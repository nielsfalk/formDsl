package de.nielsfalk.form_dsl.server

import de.nielsfalk.form_dsl.server.plugins.configureRouting
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest : StringSpec({
    "length should return size of string" {
        testApplication {
            application {
                configureRouting()
            }
            client.get("/").apply {
                status shouldBe HttpStatusCode.OK
                bodyAsText() shouldStartWith "Hello, "
                bodyAsText() shouldEndWith "!"
            }
        }

        "hello".length shouldBe 5
    }
})
