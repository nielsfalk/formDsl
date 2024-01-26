package de.nielsfalk.jsonUtil

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonBuilder

@OptIn(ExperimentalSerializationApi::class)
fun JsonBuilder.defaultJson() {
    encodeDefaults = true
    explicitNulls = false
}