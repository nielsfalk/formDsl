package de.nielsfalk.formdsl.app.data

import de.nielsfalk.jsonUtil.defaultJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createClient(): HttpClient =
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { defaultJson() })
        }
    }

actual fun localhost(): String {
    return "10.0.2.2:8080"
}