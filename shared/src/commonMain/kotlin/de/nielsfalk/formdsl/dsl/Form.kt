package de.nielsfalk.formdsl.dsl

import de.nielsfalk.bson.util.ObjectId
import de.nielsfalk.formdsl.dsl.Element.Input.BooleanInput
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectMulti
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectOne
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.dsl.Element.Label
import de.nielsfalk.formdsl.misc.FormDataValue
import de.nielsfalk.formdsl.misc.FormDataValue.StringValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val id: ObjectId,
    val title: String,
    val sections: List<Section>
)

sealed class ElementsBuilder {
    var id: String? = null
    var elements: List<Element> = emptyList()
    private val idPrefix: String?
        get() = if (this is FormBuilder) null else id

    fun selectMulti(function: SelectBuilder.() -> Unit) {
        elements += SelectBuilder(idPrefix)
            .apply(function)
            .buildMulti()
    }

    fun selectOne(function: SelectBuilder.() -> Unit) {
        elements += SelectBuilder(idPrefix)
            .apply(function)
            .buildOne()
    }

    fun label(text: String) {
        elements += Label(text)
    }

    fun textInput(function: TextInputBuilder.() -> Unit) {
        elements += TextInputBuilder(idPrefix)
            .apply(function)
            .build()
    }

    fun booleanInput(function: BooleanInputBuilder.() -> Unit = {}) {
        elements += BooleanInputBuilder(idPrefix)
            .apply(function)
            .build()
    }
}

class FormBuilder : ElementsBuilder() {
    var title: String = ""
    var sections: List<Section> = emptyList()

    fun section(function: SectionBuilder.() -> Unit) {
        id = id ?: generateNextId("section")
        sections += SectionBuilder().apply(function).build()
    }

    fun build(): Form {
        if (elements.isNotEmpty()) {
            //add default section for root elements
            sections = listOf(Section("defaultSection", elements)) + sections
        }
        return Form(
            ObjectId(id ?: throw IllegalArgumentException("id is required for form $title")),
            title,
            sections
        )
    }
}

@Serializable
data class Section(
    val id: String,
    val elements: List<Element>
)

class SectionBuilder : ElementsBuilder() {
    init {
        // id must be set early so it is known to prefix element Ids
        id = id ?: generateNextId("section")
    }

    fun build() = Section(id ?: generateNextId("section"), elements)
}

@Serializable
sealed interface Element {
    @SerialName("Label")
    @Serializable
    data class Label(val content: String) : Element

    @Serializable
    sealed interface Input : Element {
        val id: String
        val description: String?

        @Serializable
        @SerialName("TextInput")
        data class TextInput(
            override val id: String,
            override val description: String?,
            val placeholder: String?
        ) : Input

        @Serializable
        @SerialName("BooleanInput")
        data class BooleanInput(
            override val id: String,
            override val description: String?
        ) : Input

        @Serializable
        sealed interface SelectInput : Input {
            val options: List<SelectOption>

            @SerialName("SelectOne")
            @Serializable
            data class SelectOne(
                override val id: String,
                override val options: List<SelectOption>,
                override val description: String?
            ) : SelectInput

            @SerialName("SelectMulti")
            @Serializable
            data class SelectMulti(
                override val id: String,
                override val options: List<SelectOption>,
                override val description: String?
            ) : SelectInput
        }
    }
}

class TextInputBuilder(idPrefix: String?) : InputBuilder(idPrefix) {
    var placehoder: String? = null

    fun build(): TextInput =
        TextInput(
            nextId("textInput"),
            description,
            placehoder
        )
}

class BooleanInputBuilder(idPrefix: String?) : InputBuilder(idPrefix) {
    fun build(): BooleanInput =
        BooleanInput(
            nextId("textInput"),
            description
        )
}

class SelectBuilder(idPrefix: String?) : InputBuilder(idPrefix) {
    var options: List<SelectOption> = listOf()

    fun option(label: String, value: String) {
        options += SelectOption(Label(label), StringValue(value))
    }

    fun buildMulti(): SelectMulti {
        return SelectMulti(
            nextId("selectMulti"),
            options,
            description
        )
    }

    fun buildOne(): SelectOne {
        return SelectOne(
            nextId("selectOne"),
            options,
            description
        )
    }
}

abstract class InputBuilder(
    val idPrefix: String?
) {
    var id: String? = null
    var description: String? = null

    fun nextId(inputType: String) =
        id ?: generateNextId(
            listOfNotNull(idPrefix, inputType)
                .joinToString("-")
        )
}

object IdSequences {
    operator fun get(idPrefix: String): Iterator<String> {
        return prefixedIdSequence.getOrPut(idPrefix) {
            sequence {
                var counter = 0UL
                while (true) {
                    this.yield("$idPrefix$counter")
                    counter++
                }
            }.iterator()
        }
    }

    private val prefixedIdSequence: MutableMap<String, Iterator<String>> = mutableMapOf()
}

fun generateNextId(idPrefix: String): String {
    return IdSequences[idPrefix].next()
}

@Serializable
data class SelectOption(val label: Label?, val value: FormDataValue)

fun form(function: FormBuilder.() -> Unit): Form =
    FormBuilder().apply(function).build()

