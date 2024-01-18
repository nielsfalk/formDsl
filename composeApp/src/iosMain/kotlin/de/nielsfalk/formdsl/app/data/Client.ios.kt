package de.nielsfalk.formdsl.app.data

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

actual fun createClient(): HttpClient =
    HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json { encodeDefaults = true })
        }
    }