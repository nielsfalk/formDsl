package de.nielsfalk.form_dsl.server

import Greeting
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.nielsfalk.form_dsl.server.db.*
import de.nielsfalk.formdsl.forms.allForms
import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormsList
import de.nielsfalk.formdsl.misc.FormsListItem
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

fun Application.configureRouting(
    database: MongoDatabase = MongoClient.create().getDatabase("formDsl"),
    collection: MongoCollection<FormDataEntity> = database.lazyGetCollection<FormDataEntity>("formData")
) {

    install(AutoHeadResponse)
    routing {
        get("/") {
            call.respondText(Greeting().greet())
        }
        get("/forms") {
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
                    call.respond(Created, data)
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
                    call.respond(it.toModel())
                }
            }
        }
        put("/forms/{formId}/data/{formDataId}") {
            val formId = call.parameters["formId"]
            val formDataId = call.parameters["formDataId"]
            if (
                ObjectId.isValid(formId) &&
                ObjectId.isValid(formDataId) &&
                allForms.any { it.id.hexString == formId }
            ) {
                val data = call.receive<FormData>()
                collection.findById(ObjectId(formDataId))?.let { oldEntity ->
                    if (oldEntity.version == data.version) {
                        val version = oldEntity.version + 1
                        collection.updateOne(ObjectId(formDataId), data.toEntity(formDataId, formId).copy(version = version))
                        call.respond(data.copy(version = version))
                    } else
                        call.respond(Conflict, "$formDataId is outdated")
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
