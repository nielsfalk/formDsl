package de.nielsfalk.form_dsl.server

import Greeting
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.nielsfalk.form_dsl.server.db.FormDataEntity
import de.nielsfalk.form_dsl.server.db.lazyGetCollection
import de.nielsfalk.formdsl.dsl.FormData
import de.nielsfalk.formdsl.forms.allForms
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

fun Application.configureRouting(
    database: MongoDatabase = MongoClient.create().getDatabase("test"),
    collection: MongoCollection<FormDataEntity> = database.lazyGetCollection<FormDataEntity>("formData")
) {

    install(AutoHeadResponse)
    routing {
        get("/") {
            call.respondText(Greeting().greet())
        }
        get("/forms") {
            call.respond(AllFormsResponse(allForms.map { it.title }))
        }
        get("/forms/{formId}") {
            val id = call.parameters["formId"]
            val form = allForms.firstOrNull { it.id.hexString == id }
            form?.let {
                call.respond(it)
            }
        }
        post("/forms/{formId}/data") {
            val formId = call.parameters["formId"]!!
            if (
                ObjectId.isValid(formId) &&
                allForms.any { it.id.hexString == formId }
            ) {
                val data = call.receive<FormData>()
                val insertOneResult = collection.insertOne(
                    FormDataEntity(
                        formId = ObjectId(formId),
                        values = data.values
                    )
                )
                insertOneResult.insertedId?.asObjectId()?.value?.let { id ->
                    call.response.header("Location", "/forms/$formId/data/$id")
                    call.respond(HttpStatusCode.Created, data)
                }
            }
        }
        get("/forms/{formId}/data/{formDataId}") {
            val formId = call.parameters["formId"]
            val formDataId = call.parameters["formDataId"]
            if (
                ObjectId.isValid(formId) &&
                ObjectId.isValid(formDataId) &&
                allForms.any { it.id.hexString == formId }
            ) {
                collection.findByIdAndFormId(
                    ObjectId(formDataId),
                    ObjectId(formId)
                )?.let {
                    FormData(values = it.values)
                    call.respond(FormData(values = it.values))
                }
            }
        }
    }
}

suspend fun <T : Any> MongoCollection<T>.findByIdAndFormId(id: ObjectId, formId: ObjectId): T? =
    find(
        and(
            eq("_id", id),
            eq("formId", formId)
        )
    ).firstOrNull()

@Serializable
data class AllFormsResponse(
    val forms: List<String>
)
