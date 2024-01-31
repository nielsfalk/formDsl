package de.nielsfalk.form_dsl.server

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.nielsfalk.form_dsl.server.db.FormDataEntity
import de.nielsfalk.form_dsl.server.db.findById
import de.nielsfalk.form_dsl.server.db.lazyGetCollection
import de.nielsfalk.form_dsl.server.db.toEntity
import de.nielsfalk.form_dsl.server.db.updateOne
import de.nielsfalk.formdsl.dsl.Element.Input
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormDataValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.bson.types.ObjectId

class FormService(
    database: MongoDatabase = MongoClient.create().getDatabase("formDsl"),
    private val collection: MongoCollection<FormDataEntity> = database.lazyGetCollection<FormDataEntity>("formData"),
    val allForms: List<Form> = de.nielsfalk.formdsl.forms.allForms
) {
    operator fun get(id: String): Form? =
        allForms.firstOrNull { it.id.hexString == id }

    suspend fun getData(formId: String, formDataId: String): FormDataEntity? =
        if (
            ObjectId.isValid(formId) &&
            ObjectId.isValid(formDataId) &&
            allForms.any { it.id.hexString == formId }
        ) {
            collection.findByIdAndFormId(
                ObjectId(formDataId),
                ObjectId(formId)
            )
        } else null

    suspend fun insertData(formId: String, data: FormData): String? =
        if (
            ObjectId.isValid(formId) &&
            allForms.any { it.id.hexString == formId }
        ) {
            collection.insertOne(data.toEntity(formId = formId, formDataId = null))
                .insertedId?.asObjectId()?.value?.toHexString()
        } else null

    suspend fun updateData(
        formId: String,
        formDataId: String,
        data: FormData
    ): UpdateResult {
        if (
            ObjectId.isValid(formId) &&
            ObjectId.isValid(formDataId) &&
            allForms.any { it.id.hexString == formId }
        ) {
            val oldEntity = collection.findById(ObjectId(formDataId))
            if (oldEntity != null) {
                return if (oldEntity.version == data.version) {
                    val version = oldEntity.version + 1
                    collection.updateOne<FormDataEntity>(
                        id = ObjectId(formDataId),
                        entity = data.toEntity(
                            formDataId = formDataId,
                            formId = formId,
                            version = version
                        )
                    )
                    UpdateResult(version = version)
                } else UpdateResult(outdated = true)
            }
        }
        return UpdateResult()
    }

    suspend fun evaluate(formId: String): Pair<List<String>, Flow<List<Any>>>? =
        if (
            ObjectId.isValid(formId)

        ) {
            allForms.firstOrNull { it.id.hexString == formId }
                ?.let { form ->
                    val inputElements = form.sections
                        .flatMap { it.elements }
                        .mapNotNull { it as? Input }
                    val headers =
                        listOf("created") + inputElements.map { "${it.id} ${it.description}" } + "platform"
                    val rowFlow = collection.findByFormId(ObjectId(formId))
                        .map { entry ->
                            listOf(
                                Instant.fromEpochSeconds(entry.id!!.timestamp.toLong()).toString()
                            ) +
                                    inputElements.map {
                                        when (val formDataValue = entry.values[it.id]){
                                            is FormDataValue.ListValue -> formDataValue.value.map(FormDataValue::value)
                                            null -> ""
                                            else -> formDataValue.value
                                        }
                                    } +
                                    entry.platform
                        }
                    headers to rowFlow
                }
        } else null

    data class UpdateResult(
        val outdated: Boolean = false,
        val version: Long? = null
    )
}


suspend fun <T : Any> MongoCollection<T>.findByIdAndFormId(id: ObjectId, formId: ObjectId): T? =
    find(
        Filters.and(
            Filters.eq("_id", id),
            Filters.eq("formId", formId)
        )
    ).firstOrNull()

fun <T : Any> MongoCollection<T>.findByFormId(formId: ObjectId): FindFlow<T> =
    find(
        Filters.and(
            Filters.eq("formId", formId)
        )
    )