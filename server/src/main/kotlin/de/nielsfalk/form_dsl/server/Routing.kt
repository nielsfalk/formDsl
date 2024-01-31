package de.nielsfalk.form_dsl.server

import Greeting
import de.nielsfalk.form_dsl.server.db.toModel
import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormsList
import de.nielsfalk.formdsl.misc.FormsListItem
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import kotlinx.html.*

fun Application.configureRouting(
    service: FormService = FormService()
) {

    install(AutoHeadResponse)
    routing {
        get("/") {
            call.respondText(Greeting().greet())
        }

        get("/forms") {
            val allForms = service.allForms

            call.respond(
                FormsList(
                    allForms.map {
                        FormsListItem(
                            id = it.id.hexString,
                            title = it.title
                        )
                    })
            )
        }

        get("/forms/{formId}") {
            service[call.parameters["formId"]!!]
                ?.let {
                    call.respond(it)
                }
        }

        post("/forms/{formId}/data") {
            val formId = call.parameters["formId"]!!
            val data = call.receive<FormData>()

            service.insertData(
                formId = formId,
                data = data
            )
                ?.let { id ->
                    call.response.header("Location", "/forms/$formId/data/$id")
                    call.respond(Created, data)
                }
        }

        get("/forms/{formId}/data/{formDataId}") {
            service.getData(
                formId = call.parameters["formId"]!!,
                formDataId = call.parameters["formDataId"]!!
            )
                ?.let {
                    call.respond(it.toModel())
                }
        }

        put("/forms/{formId}/data/{formDataId}") {
            val formDataId = call.parameters["formDataId"]!!
            val data = call.receive<FormData>()

            val result = service.updateData(
                formId = call.parameters["formId"]!!,
                formDataId = formDataId,
                data = data
            )

            if (result.outdated)
                call.respond(Conflict, "$formDataId is outdated")
            else result.version?.let { call.respond(data.copy(version = it)) }
        }

        get("/forms/{formId}/evaluation") {
            service.evaluate(call.parameters["formId"]!!)?.let { (headers, rowFlow) ->
                val rows = rowFlow.toList()
                call.respondHtml {
                    head {
                        style {
                            +tableStyle
                        }
                    }
                    body {
                        table {
                            thead {
                                tr {
                                    headers.forEach {
                                        th {
                                            +it
                                        }
                                    }
                                }
                            }
                            tbody {
                                rows.forEach { row ->
                                    tr {
                                        row.forEach { value ->
                                            td {
                                                when (value) {
                                                    is List<*> -> {
                                                        value.forEach {
                                                            p { +it.toString() }
                                                        }
                                                    }

                                                    else -> {
                                                        +value.toString()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val tableStyle = Routing::class.java.getResource("/tableStyle.css")!!.readText()

