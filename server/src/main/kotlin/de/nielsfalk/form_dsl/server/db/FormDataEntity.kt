package de.nielsfalk.form_dsl.server.db

import de.nielsfalk.formdsl.dsl.FormData
import de.nielsfalk.formdsl.dsl.FormDataValue
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class FormDataEntity(
    @SerialName("_id")
    @Contextual
    val id: ObjectId? = null,
    @Contextual
    val formId: ObjectId,
    val values: Map<String, FormDataValue>,
    val version: Long = 0
)

fun FormData.toEntity(
    formDataId: String?,
    formId: String?
): FormDataEntity =
    FormDataEntity(
        id = ObjectId(formDataId),
        formId = ObjectId(formId),
        values = values
    )

fun FormDataEntity.toModel(): FormData =
    FormData(
        values = values,
        version = version
    )