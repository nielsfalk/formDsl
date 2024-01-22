package de.nielsfalk.formdsl.app.data

import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.misc.FormsList
import de.nielsfalk.formdsl.misc.FormsListItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*

class FormsRepository : Closeable {
    private var _client: HttpClient? = null
    val client: HttpClient by lazy { _client ?: createClient().also { _client = it } }
    override fun close() {
        _client?.close()
    }

    suspend fun getAvailableForms(): List<FormsListItem> =
        client.get("http://${localhost()}/forms").body<FormsList>().forms

    suspend fun getForm(id: String): Form =
        client.get("http://${localhost()}/forms/$id").body<Form>()
}
