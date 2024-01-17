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
        createClient().get("http://ip.jsontest.com/").apply {
            status shouldBe HttpStatusCode.OK
            body<IpResponse>().ip.shouldNotBeEmpty()
        }
    }
})

@Serializable
data class IpResponse(
    val ip: String
)
