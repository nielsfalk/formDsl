package de.nielsfalk.formdsl.list

import kotlinx.serialization.Serializable

@Serializable
data class FormsList(
    val forms: List<FormsListItem>
)

@Serializable
data class FormsListItem(
    val id:String,
    val title:String
)