package de.nielsfalk.formdsl.app.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class ClientTest : StringSpec({
    "test read json" {
        client.get("http://ip.jsontest.com/").apply {
            status shouldBe HttpStatusCode.OK
            body<AllFormsResponse>().ip.shouldNotBeEmpty()
        }
    }
})

@Serializable
data class AllFormsResponse(
    val ip: String
)
