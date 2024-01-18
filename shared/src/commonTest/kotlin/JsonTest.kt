import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonTest : FreeSpec({

    val json = Json { encodeDefaults = true }

    "with map attribute" - {
        "encode" {
            val encodeToString = json.encodeToString(JsonContainsMapTest(mapOf("foo" to "bar")))

            encodeToString shouldBe """{"map":{"foo":"bar"}}"""

        }

        "decode" {
            val jsonString = """{"map":{"foo":"bar"}}"""

            val decodedFoo = json.decodeFromString<JsonContainsMapTest>(jsonString)

            decodedFoo shouldBe JsonContainsMapTest(mapOf("foo" to "bar"))
        }
    }

    "with inheritance" - {
        "encode discriminating" {
            val list = listOf(
                JsonDiscriminatorTestBase.AnImpl(),
                JsonDiscriminatorTestBase.AnotherImpl()
            )

            val encodeToString = json.encodeToString(list)

            encodeToString shouldBe """[{"type":"One","foo":"bar"},{"type":"Two","foo":"bar"}]"""

        }


        "decode via discriminator" {
            val jsonString = """[{"type":"One"},{"type":"Two"}]"""

            val decodedFoo = json.decodeFromString<List<JsonDiscriminatorTestBase>>(jsonString)

            decodedFoo shouldBe listOf(JsonDiscriminatorTestBase.AnImpl(), JsonDiscriminatorTestBase.AnotherImpl())
        }
    }
})

@Serializable
data class JsonContainsMapTest(
    val map: Map<String, String>? = null
)

@Serializable
sealed interface JsonDiscriminatorTestBase {
    @SerialName("One")
    @Serializable
    data class AnImpl(val foo: String = "bar") : JsonDiscriminatorTestBase

    @SerialName("Two")
    @Serializable
    data class AnotherImpl(val foo: String = "bar") : JsonDiscriminatorTestBase
}