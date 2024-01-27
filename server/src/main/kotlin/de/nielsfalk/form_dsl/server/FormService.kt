package de.nielsfalk.form_dsl.server

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.nielsfalk.form_dsl.server.db.*
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.misc.FormData
import kotlinx.coroutines.flow.firstOrNull
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
            collection.insertOne(
                FormDataEntity(
                    formId = ObjectId(formId),
                    values = data.values
                )
            )
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
            de.nielsfalk.formdsl.forms.allForms.any { it.id.hexString == formId }
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