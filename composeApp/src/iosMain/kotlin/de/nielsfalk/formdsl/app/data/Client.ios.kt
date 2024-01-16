package de.nielsfalk.formdsl.app.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

actual val client: HttpClient
    get() = HttpClient(Darwin) {
        install(ContentNegotiation){
            json()
        }
    }