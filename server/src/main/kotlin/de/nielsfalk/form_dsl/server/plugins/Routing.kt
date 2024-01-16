package de.nielsfalk.form_dsl.server.plugins

import Greeting
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(AutoHeadResponse)
    routing {
        get("/") {
            call.respondText(Greeting().greet())
        }
    }
}
