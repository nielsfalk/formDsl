package de.nielsfalk.bson.util

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import org.bson.types.ObjectId as MongoBsonObjectId


class ObjectIdTest : FreeSpec({
    val aHexString: String = MongoBsonObjectId().toHexString()
    val validHexStrings = listOf(
        aHexString,
        "141C4E533AA94A658070BB32",
        "52F9A8E05D824F6FB8892762",
        "13D33EE96A24446AAB89B59B",
        "15eb75be5a33437eac8ac84e",
        "05d8ef95917c4cc5a68d58a2",
        "27b9fdc618644e18a3286665",
        "f48766c78e7a408f9015dfee",
        "8c24a93c93a0411eb1742f7c",
        "da97bdca66134d5e93fbf1e2",
        "595dbbe9fb6c453980f79294",
        "e93bf8ff6679476a9d96f9e9",
        "4a769b25984944d68ce5cbd4",
        "c230f4011ef74d06bc2225f0",
        "880265bf6e7240f69983dd2f",
        "a4fa770a17a843da83b7c9c9",
        "f8b482dc1b2a4efb9487dd5b",
        "6591964289204d718e3dc68f",
        "1fadd2f8fb86478c94c6580f",
        "5e8a5a88af604c0d8124d853",
        "b50e9c2ecf9344d882532d23",
        "156c0a36fbc84f2892aea702",
        "e921ed042de349189879f586",
        "383547739a90442fb8f5d12d",
        "082c959736bb442b98ade478",
        "3eb8fcaf85e64665af6453fc",
        "e8eae9480c8140c8b90603ee",
        "391071eb835d4d8abecab2a6"
    )

    val invalidHexStrings = listOf(
        "141C4E533AA94A658070BB",
        "52F9A8E05D824F6FB889276",
        "13D33EE96A24446AAB89B59B0",
        "15eb75be5a33437eac8ac84e01",
        "G5d8ef95917c4cc5a68d58a2",
        "27b9Vdc618644e18a3286665",
        ""
    )

    "valid hexString" - {
        validHexStrings.forEach {
            "$it is valid" {
                ObjectId.isValid(it) shouldBe true
                MongoBsonObjectId.isValid(it) shouldBe true
            }
        }
    }

    "invalid hexString" - {
        invalidHexStrings.forEach {
            "$it is invalid" {
                ObjectId.isValid(it) shouldBe false
                MongoBsonObjectId.isValid(it) shouldBe false
            }
        }
    }

    "ObjectId(valid HexString)" - {
        validHexStrings.forEach {
            """ObjectId("$it") properties equal MongoBsonObjectId("$it")""" {
                val objectId = ObjectId(it)

                val mongoBsonObjectId = MongoBsonObjectId(it)
                objectId.timestamp shouldBe mongoBsonObjectId.timestamp.toUInt()
                objectId.byteArray shouldBe mongoBsonObjectId.toByteArray()
                objectId.originalHashCode() shouldBe mongoBsonObjectId.hashCode()
            }
        }
    }

    "ObjectId(epochSeconds)"{
        val timestamp= 1705621867.toUInt()
        val objectId = ObjectId(timestamp)

        val mongoBsonObjectId = MongoBsonObjectId(Date(timestamp.toLong()*1000))
        objectId.timestamp shouldBe mongoBsonObjectId.timestamp.toUInt()
    }

    "ObjectId()"{
        val objectId = ObjectId()

        val mongoBsonObjectId = MongoBsonObjectId(objectId.hexString)
        objectId.timestamp shouldBe mongoBsonObjectId.timestamp.toUInt()
        objectId.byteArray shouldBe mongoBsonObjectId.toByteArray()
        objectId.originalHashCode() shouldBe mongoBsonObjectId.hashCode()
    }

    @Serializable
    data class Foo(val id: ObjectId )

    "serialize ObjectId"{
        val json = Json.encodeToString(Foo(ObjectId(validHexStrings.first())))

        json shouldBe """{"id":"${validHexStrings.first()}"}"""
    }

    "deserialize ObjectId"{
        val anObjectWithId = Json.decodeFromString<Foo>("""{"id":"${validHexStrings.first()}"}""")

        anObjectWithId.id.hexString shouldBe validHexStrings.first()
    }

})

