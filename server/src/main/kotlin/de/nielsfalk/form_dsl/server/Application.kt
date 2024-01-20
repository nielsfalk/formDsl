package de.nielsfalk.form_dsl.server

import SERVER_PORT
import de.nielsfalk.form_dsl.server.plugins.configureHTTP
import de.nielsfalk.form_dsl.server.plugins.configureMonitoring
import de.nielsfalk.form_dsl.server.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
