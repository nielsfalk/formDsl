package de.nielsfalk.form_dsl.server.plugins

import Greeting
import de.nielsfalk.formdsl.forms.allForms
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(AutoHeadResponse)
    routing {
        get("/") {
            call.respondText(Greeting().greet())
        }
        get("/forms") {
            call.respond(AllFormsResponse(allForms.map { it.title }))
        }
    }
}

@Serializable
data class AllFormsResponse(
    val forms:List<String>
)
