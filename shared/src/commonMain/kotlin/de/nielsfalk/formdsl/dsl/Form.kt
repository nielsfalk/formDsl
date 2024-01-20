package de.nielsfalk.formdsl.dsl

import de.nielsfalk.formdsl.dsl.Element.Label
import de.nielsfalk.formdsl.dsl.Element.SelectMulti
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val id: String,
    val title: String,
    val sections: List<Section>
)

sealed class ElementsBuilder {
    var elements: List<Element> = emptyList()
    fun selectMulti(function: SelectBuilder.() -> Unit) {
        elements += SelectBuilder().apply(function).buildMulti()
    }

    fun label(text: String) {
        elements += Label(text)
    }
}

class FormBuilder : ElementsBuilder() {
    var id: String? = null
    var title: String = ""
    var sections: List<Section> = emptyList()

    fun section(function: SectionBuilder.() -> Unit) {
        sections += SectionBuilder().apply(function).build()
    }

    fun build(): Form {
        if (elements.isNotEmpty()) {
            //add default section for root elements
            sections = listOf(Section(elements)) + sections
        }
        return Form(
            id ?: throw IllegalArgumentException("id is required for form $title"),
            title,
            sections
        )
    }
}

@Serializable
data class Section(
    val elements: List<Element>
)

class SectionBuilder : ElementsBuilder() {
    fun build() = Section(elements)
}

@Serializable
sealed interface Element {
    @SerialName("Label")
    @Serializable
    data class Label(val content: String) : Element

    @Serializable
    sealed interface SelectElement : Element {
        val options: List<SelectOption>
    }

    @SerialName("SelectOne")
    @Serializable
    data class SelectOne(override val options: List<SelectOption>) : SelectElement

    @SerialName("SelectMulti")
    @Serializable
    data class SelectMulti(override val options: List<SelectOption>) : SelectElement
}


class SelectBuilder() {
    var options: List<SelectOption> = listOf()

    fun option(label: String, value: String) {
        options += SelectOption(Label(label), value)
    }

    fun buildMulti(): SelectMulti = SelectMulti(options)
}


@Serializable
data class SelectOption(val label: Label, val value: String)

fun form(function: FormBuilder.() -> Unit): Form =
    FormBuilder().apply(function).build()

