package de.nielsfalk.formdsl.app.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual val client: HttpClient
    get() = HttpClient(OkHttp){
        install(ContentNegotiation){
            json()
        }
    }