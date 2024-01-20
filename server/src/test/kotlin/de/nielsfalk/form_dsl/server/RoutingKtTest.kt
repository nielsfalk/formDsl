package de.nielsfalk.form_dsl.server

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import de.nielsfalk.form_dsl.server.db.FormDataEntity
import de.nielsfalk.form_dsl.server.db.findById
import de.nielsfalk.form_dsl.server.db.lazyGetCollection
import de.nielsfalk.form_dsl.server.plugins.configureSerialization
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.dsl.FormData
import de.nielsfalk.formdsl.dsl.FormDataValue
import de.nielsfalk.formdsl.forms.allForms
import de.nielsfalk.formdsl.forms.noodleId
import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.contain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import kotlin.time.Duration

class RoutingKtTest : FreeSpec({
    "GET /forms".withTestApp {
        client.get("/forms").apply {

            status shouldBe OK
            body<AllFormsResponse>().forms should contain("a noodle survey")
        }
    }
    "GET /form/{id}".withTestApp {
        val id = noodleId

        client.get("/forms/$id").apply {

            status shouldBe OK
            body<Form>() shouldBe allForms.first { it.id.hexString == id }
        }
    }

    var formDataId: String?
    "POST /forms/{formId}/data".withTestApp {
        val formData = FormData(mapOf("foo" to FormDataValue(string = "bar")))

        client.post("/forms/$noodleId/data") {
            setBody(formData)
            contentType(ContentType.Application.Json)
        }.apply {
            formDataId = headers["Location"]?.split("/data/")?.get(1)

            status shouldBe HttpStatusCode.Created
            body<FormData>() shouldBe formData
            headers["Location"] shouldStartWith "/forms/$noodleId/data/"
            collection.findById(ObjectId(formDataId!!)) shouldBe FormDataEntity(
                id = ObjectId(formDataId),
                formId = ObjectId(noodleId),
                values = formData.values
            )
        }
    }
})

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

        testApplication {
            application {
                configureSerialization()

                configureRouting(
                    database,
                    collection
                )
            }
            val client = createClient {
                install(ContentNegotiation) {
                    json(Json { encodeDefaults = true })
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
