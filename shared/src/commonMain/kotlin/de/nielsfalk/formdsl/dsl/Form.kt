package de.nielsfalk.formdsl.dsl

import de.nielsfalk.bson.util.ObjectId
import de.nielsfalk.formdsl.dsl.Element.Input.BooleanInput
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectMulti
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectOne
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.dsl.Element.Label
import de.nielsfalk.formdsl.misc.FormDataValue
import de.nielsfalk.formdsl.misc.FormDataValue.BooleanValue
import de.nielsfalk.formdsl.misc.FormDataValue.Companion
import de.nielsfalk.formdsl.misc.FormDataValue.ListValue
import de.nielsfalk.formdsl.misc.FormDataValue.LocalDateTimeValue
import de.nielsfalk.formdsl.misc.FormDataValue.LocalDateValue
import de.nielsfalk.formdsl.misc.FormDataValue.StringValue
import de.nielsfalk.formdsl.misc.of
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val id: ObjectId,
    val title: String,
    val sections: List<Section>
)

sealed class ElementsBuilder(
    private val generateNextId: (String) -> String
) {
    var id: String? = null
    var elements: List<Element> = emptyList()
    private val idPrefix: String?
        get() = when (this) {
            is FormBuilder -> null
            is SectionBuilder -> {
                if (id == null) {
                    id = generateNextId("section")
                }
                id
            }
        }

    fun selectMulti(function: SelectBuilder.() -> Unit) {
        elements += SelectBuilder(idPrefix, generateNextId)
            .apply(function)
            .run {
                SelectMulti(
                    nextId("selectMulti"),
                    options,
                    description,
                    defaultValue?.let(Companion::of)
                        ?.let {
                            if (it is ListValue) it
                            else ListValue(listOf(it))
                        }
                )
            }
    }

    fun selectOne(function: SelectBuilder.() -> Unit) {
        elements += SelectBuilder(idPrefix, generateNextId)
            .apply(function)
            .run {
                SelectOne(
                    nextId("selectOne"),
                    options,
                    description,
                    defaultValue?.let(Companion::of)
                )
            }
    }

    fun label(text: String) {
        elements += Label(text)
    }

    fun textInput(function: TextInputBuilder.() -> Unit) {
        elements += TextInputBuilder(idPrefix, generateNextId)
            .apply(function)
            .run {
                TextInput(
                    nextId("textInput"),
                    description,
                    placehoder,
                    defaultValue?.let(::StringValue)
                )
            }
    }

    fun booleanInput(function: BooleanInputBuilder.() -> Unit = {}) {
        elements += BooleanInputBuilder(idPrefix, generateNextId)
            .apply(function)
            .run {
                BooleanInput(
                    nextId("textInput"),
                    description,
                    defaultValue?.let(::BooleanValue)
                )
            }
    }
}

class FormBuilder(private val generateNextId: (String) -> String) :
    ElementsBuilder(generateNextId) {
    var title: String = ""
    var sections: List<Section> = emptyList()

    fun section(function: SectionBuilder.() -> Unit) {
        sections += SectionBuilder(generateNextId)
            .apply(function)
            .run { Section(id ?: generateNextId("section"), elements) }
    }
}

@Serializable
data class Section(
    val id: String,
    val elements: List<Element>
)

class SectionBuilder(generateNextId: (String) -> String) :
    ElementsBuilder(generateNextId)

@Serializable
sealed interface Element {
    @SerialName("Label")
    @Serializable
    data class Label(val content: String) : Element

    @Serializable
    sealed interface Input : Element {
        val id: String
        val description: String?
        val defaultValue: FormDataValue?

        @Serializable
        @SerialName("TextInput")
        data class TextInput(
            override val id: String,
            override val description: String?,
            val placeholder: String?,
            override val defaultValue: StringValue?
        ) : Input

        @Serializable
        @SerialName("BooleanInput")
        data class BooleanInput(
            override val id: String,
            override val description: String?,
            override val defaultValue: BooleanValue?
        ) : Input

        @Serializable
        sealed interface SelectInput : Input {
            val options: List<SelectOption>

            @SerialName("SelectOne")
            @Serializable
            data class SelectOne(
                override val id: String,
                override val options: List<SelectOption>,
                override val description: String?,
                override val defaultValue: FormDataValue?
            ) : SelectInput

            @SerialName("SelectMulti")
            @Serializable
            data class SelectMulti(
                override val id: String,
                override val options: List<SelectOption>,
                override val description: String?,
                override val defaultValue: ListValue?
            ) : SelectInput
        }
    }
}

class TextInputBuilder(idPrefix: String?, generateNextId: (String) -> String) :
    InputBuilder(idPrefix, generateNextId) {
    var placehoder: String? = null
    var defaultValue: String? = null
}

class BooleanInputBuilder(idPrefix: String?, generateNextId: (String) -> String) :
    InputBuilder(idPrefix, generateNextId) {
    var defaultValue: Boolean? = null
}

class SelectBuilder(idPrefix: String?, generateNextId: (String) -> String) :
    InputBuilder(idPrefix, generateNextId) {
    var defaultValue: Any? = null
    var options: List<SelectOption> = listOf()

    fun option(value: String, label: String? = null) {
        options += SelectOption(label?.let(::Label), StringValue(value))
    }

    fun option(value: LocalDateTime) {
        options += SelectOption(null, LocalDateTimeValue(value))
    }

    fun option(value: LocalDate) {
        options += SelectOption(null, LocalDateValue(value))
    }
}

abstract class InputBuilder(
    private val idPrefix: String?,
    private val generateNextId: (String) -> String
) {
    var id: String? = null
    var description: String? = null

    fun nextId(inputType: String) =
        id ?: generateNextId(idPrefix?.let { "$it-$inputType" } ?: inputType)
}

@Serializable
data class SelectOption(val label: Label?, val value: FormDataValue)

fun form(function: FormBuilder.() -> Unit): Form {
    val prefixedIdSequence: MutableMap<String, Iterator<String>> = mutableMapOf()
    val generateNextId: (String) -> String = {
        prefixedIdSequence.getOrPut(it) {
            sequence {
                var counter = 0UL
                while (true) {
                    this.yield("$it$counter")
                    counter++
                }
            }.iterator()
        }.next()
    }
    return FormBuilder(generateNextId).apply(function).run {
        if (elements.isNotEmpty()) {
            //add default section for root elements
            sections = listOf(Section("defaultSection", elements)) + sections
        }
        Form(
            ObjectId(id ?: throw IllegalArgumentException("id is required for form $title")),
            title,
            sections
        ).also(Form::ensureUniqueIds)
    }
}

fun Form.ensureUniqueIds() {
    val allIds = sections.map { it.id } +
            sections.flatMap { it.elements }
                .mapNotNull { (it as? Element.Input)?.id }

    val duplicateIds = allIds.groupBy { it }.filter { it.value.size > 1 }.keys
    if (duplicateIds.isNotEmpty()) {
        throw IllegalArgumentException("ids $duplicateIds were used multiple times")
    }
}