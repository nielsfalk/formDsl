package de.nielsfalk.form_dsl.server

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.nielsfalk.form_dsl.server.db.FormDataEntity
import de.nielsfalk.form_dsl.server.db.findById
import de.nielsfalk.form_dsl.server.db.lazyGetCollection
import de.nielsfalk.form_dsl.server.plugins.configureSerialization
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.dsl.form
import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormDataValue.ListValue
import de.nielsfalk.formdsl.misc.FormDataValue.LocalDateTimeValue
import de.nielsfalk.formdsl.misc.FormDataValue.StringValue
import de.nielsfalk.formdsl.misc.FormsList
import de.nielsfalk.formdsl.misc.FormsListItem
import de.nielsfalk.jsonUtil.defaultJson
import getPlatform
import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.time.Duration

class RoutingKtTest : FreeSpec({
    "GET /forms".withTestApp {
        client.get("/forms").apply {

            status shouldBe OK
            body<FormsList>() shouldBeEqual FormsList(
                listOf(
                    FormsListItem(
                        testFormId,
                        "a Noodle survey"
                    )
                )
            )
        }
    }

    "GET /form/{id}".withTestApp {
        val id = testFormId

        client.get("/forms/$id").apply {

            status shouldBe OK
            body<Form>() shouldBe testForm
        }
    }

    val formData = FormData(
        values = mapOf(
            "name" to StringValue(value = "Niels Falk"),
            "noodleSection-selectMulti0" to ListValue(
                listOf(
                    "2024-08-30T18:43".toLocalDateTime(),
                    "2024-08-31T18:43".toLocalDateTime()
                ).map(::LocalDateTimeValue)
            )

        )
    )
    var formDataId: String? = null

    "POST /forms/{formId}/data".withTestApp {
        client.post("/forms/$testFormId/data") {
            setBody(formData)
            contentType(ContentType.Application.Json)
        }.apply {
            formDataId = headers["Location"]?.split("/data/")?.get(1)

            status shouldBe HttpStatusCode.Created
            body<FormData>() shouldBe formData
            headers["Location"] shouldStartWith "/forms/$testFormId/data/"
            collection.findById(ObjectId(formDataId!!)) shouldBe FormDataEntity(
                id = ObjectId(formDataId),
                formId = ObjectId(testFormId),
                values = formData.values,
                platform = getPlatform().name
            )
        }
    }

    "GET /forms/{formId}/data/{formDataId}".withTestApp {
        client.get("/forms/$testFormId/data/$formDataId").apply {

            status shouldBe OK
            body<FormData>() shouldBe formData
        }
    }

    "PUT /forms/{formId}/data/{formDataId}".withTestApp {
        val updateData =
            formData.copy(values = formData.values + ("name" to StringValue("Niels J. Falk")))

        client.put("/forms/$testFormId/data/$formDataId") {
            setBody(updateData)
            contentType(ContentType.Application.Json)
        }.apply {

            status shouldBe OK
            body<FormData>() shouldBe updateData.copy(version = 1)
            collection.findById(ObjectId(formDataId!!)) shouldBe FormDataEntity(
                id = ObjectId(formDataId),
                formId = ObjectId(testFormId),
                values = updateData.values,
                platform = getPlatform().name,
                version = 1
            )
        }
    }

    "PUT /forms/{formId}/data/{formDataId} outdated".withTestApp {
        val updateData = formData.copy(values = mapOf("foo" to StringValue("outdated")))

        client.put("/forms/$testFormId/data/$formDataId") {
            setBody(updateData)
            contentType(ContentType.Application.Json)
        }.apply {

            status shouldBe Conflict
            body<String>() shouldBe "$formDataId is outdated"
        }
    }

    "GET /forms/{formId}/evaluation".withTestApp {
        client.get("/forms/$testFormId/evaluation")
            .apply {

                status shouldBe OK
                bodyAsText().apply {
                    shouldContain("<th>created</th>")
                    shouldContain("<th>name Please enter your name</th>")
                    shouldContain("<th>noodleSection-selectMulti0 Do you have time on</th>")
                    shouldContain("<th>platform</th>")
                    shouldContain("<td>${Instant.fromEpochSeconds(ObjectId(formDataId).timestamp.toLong())}</td>")
                    shouldContain("<td>Niels J. Falk</td>")
                    shouldContain("<p>2024-08-30T18:43</p>")
                    shouldContain("<p>2024-08-31T18:43</p>")
                    shouldContain("<td>Java")
                }
            }
    }
})

const val testFormId = "65bc236ecbe35e78d678cfd5"

val testForm = form {
    id = testFormId
    title = "a Noodle survey"
    label("Noodle is something like Doodle")
    section {
        id = "noodleSection"
        textInput {
            id = "name"
            description = "Please enter your name"
            placehoder = "Name"
        }

        selectMulti {
            description = "Do you have time on"
            option("2024-08-30T18:43".toLocalDateTime())
            option("2024-08-31T18:43".toLocalDateTime())
        }
    }
}


context (FreeSpec)
private fun String.withTestApp(
    enabled: Boolean? = null,
    invocations: Int? = null,
    threads: Int? = null,
    tags: Set<Tag>? = null,
    timeout: Duration? = null,
    extensions: List<TestCaseExtension>? = null,
    enabledIf: EnabledIf? = null,
    invocationTimeout: Duration? = null,
    severity: TestCaseSeverityLevel? = null,
    failfast: Boolean? = null,
    blockingTest: Boolean? = null,
    coroutineTestScope: Boolean? = null,
    function: suspend TestScopeWithClientAndDb.() -> Unit
) {
    config(
        enabled,
        invocations,
        threads,
        tags,
        timeout,
        extensions,
        enabledIf,
        invocationTimeout,
        severity,
        failfast,
        blockingTest,
        coroutineTestScope

    ) {
        val database = MongoClient.create().getDatabase("test")
        val collection = database.lazyGetCollection<FormDataEntity>("formData")
        val service = FormService(
            database,
            collection,
            allForms = listOf(testForm)
        )

        testApplication {
            application {
                configureSerialization()
                configureRouting(service)
            }
            val client = createClient {
                install(ContentNegotiation) {
                    json(Json { defaultJson() })
                }
            }
            TestScopeWithClientAndDb(
                this@config,
                client,
                database,
                collection
            ).function()
        }
    }
}

class TestScopeWithClientAndDb(
    testScope: TestScope,
    val client: HttpClient,
    val database: MongoDatabase,
    val collection: MongoCollection<FormDataEntity>
) : TestScope by testScope
