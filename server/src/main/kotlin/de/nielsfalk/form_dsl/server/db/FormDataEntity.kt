package de.nielsfalk.form_dsl.server.db

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
    val values: Map<String, FormDataValue>
)