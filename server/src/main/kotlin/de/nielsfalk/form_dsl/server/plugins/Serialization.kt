package de.nielsfalk.form_dsl.server.plugins

import de.nielsfalk.jsonUtil.defaultJson
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json { defaultJson() })
    }
}
