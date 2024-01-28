package de.nielsfalk.form_dsl.server.db

import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormDataValue
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
    val platform: String,
    val version: Long = 0
)

fun FormData.toEntity(
    formDataId: String?,
    formId: String?,
    version: Long = 0
): FormDataEntity =
    FormDataEntity(
        id = formDataId?.let(::ObjectId),
        formId = ObjectId(formId),
        values = values,
        platform = platform,
        version = version
    )

fun FormDataEntity.toModel(): FormData =
    FormData(
        values = values,
        version = version
    )