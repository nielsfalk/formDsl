package de.nielsfalk.form_dsl.server.db

import de.nielsfalk.formdsl.dsl.FormDataValue
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import org.bson.types.ObjectId

data class FormDataEntity(
    @SerialName("_id")
    @Contextual
    val id: ObjectId? = null,
    val values: Map<String, FormDataValue>
)