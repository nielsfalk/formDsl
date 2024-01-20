package de.nielsfalk.form_dsl.server

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
        get("/forms/{id}") {
            val id = call.parameters["id"]
            val form = allForms.firstOrNull{ it.id.hexString == id }
            form?.let {
                call.respond(it)
            }
        }
    }
}

@Serializable
data class AllFormsResponse(
    val forms: List<String>
)
