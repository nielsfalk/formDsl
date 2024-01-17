package de.nielsfalk.formdsl.app.data

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual fun createClient(): HttpClient =
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }