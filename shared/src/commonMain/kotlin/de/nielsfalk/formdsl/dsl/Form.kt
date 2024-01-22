package de.nielsfalk.formdsl.dsl

import de.nielsfalk.bson.util.ObjectId
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectMulti
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.dsl.Element.Label
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val id: ObjectId,
    val title: String,
    val sections: List<Section>
)

sealed class ElementsBuilder {
    open var id: String? = null
    var elements: List<Element> = emptyList()
    fun selectMulti(function: SelectBuilder.() -> Unit) {
        elements += SelectBuilder(idPrefix = if (this is FormBuilder) null else id)
            .apply(function)
            .buildMulti()
    }

    fun label(text: String) {
        elements += Label(text)
    }

    fun textInput(function: TextInputBuilder.() -> Unit){
        elements += TextInputBuilder(idPrefix = if (this is FormBuilder) null else id)
            .apply(function)
            .build()
    }
}

class FormBuilder : ElementsBuilder() {
    override var id: String? = null
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
            ObjectId(id ?: throw IllegalArgumentException("id is required for form $title")),
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
    sealed interface Input : Element {
        val id: String

        @Serializable
        @SerialName("TextInput")
        data class TextInput(
            override val id: String,
            val placeholder: String? = null
        ) : Input

        @Serializable
        sealed interface SelectInput : Input {
            val options: List<SelectOption>

            @SerialName("SelectOne")
            @Serializable
            data class SelectOne(
                override val options: List<SelectOption>,
                override val id: String
            ) : SelectInput

            @SerialName("SelectMulti")
            @Serializable
            data class SelectMulti(
                override val id: String,
                override val options: List<SelectOption>
            ) : SelectInput
        }
    }
}

class TextInputBuilder(idPrefix: String?) : InputBuilder(idPrefix) {
    var placehoder: String? = null

    fun build(): TextInput =
        TextInput(
            this.id ?: listOfNotNull(idPrefix, nextId("testInput")).joinToString("-"),
            placehoder
        )
}

class SelectBuilder(idPrefix: String?) : InputBuilder(idPrefix) {
    var options: List<SelectOption> = listOf()

    fun option(label: String, value: String) {
        options += SelectOption(Label(label), value)
    }

    fun buildMulti(): SelectMulti {
        return SelectMulti(
            this.id ?: listOfNotNull(idPrefix, nextId("selectMulti")).joinToString("-"),
            options
        )
    }
}

abstract class InputBuilder(val idPrefix: String?) {
    var id: String? = null
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

fun nextId(idPrefix: String): String {
    return IdSequences[idPrefix].next()
}

@Serializable
data class SelectOption(val label: Label, val value: String)

fun form(function: FormBuilder.() -> Unit): Form =
    FormBuilder().apply(function).build()

