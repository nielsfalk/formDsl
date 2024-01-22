package de.nielsfalk.form_dsl.server.db

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import kotlin.random.Random
import kotlin.random.nextUInt

class MongoExtensionsKtTest : FreeSpec({
    val databaseName = "test${Random.nextUInt()}"
    val database: MongoDatabase = MongoClient.create().getDatabase(databaseName)

    @Serializable
    data class Jedi(
        @SerialName("_id")
        @Contextual
        val id: ObjectId? = null,
        val version: Long = 0,
        val name: String,
        val age: Int
    )

    val collection = database.lazyGetCollection<Jedi>("jedi")
    var jedi = Jedi(name = "Luke", age = 17)
    val jediId = runBlocking { collection.insertOne(jedi).insertedId!!.objectId }
    jedi = jedi.copy(id = jediId)

    "findById" {
        val found = collection.findById(jediId)

        found shouldBe jedi
    }

    "updateOne" {
        collection.updateOneWithAutoVersion(jediId, jedi.copy(age = 19))

        collection.findById(jediId)!!.apply {
            age shouldBe 19
            version shouldBe 1
        }
    }


    "updateOutdated" {
        val updateResult = collection.updateOneWithAutoVersion(jediId, jedi)

        updateResult.modifiedCount shouldBe 0L
    }


    afterProject { runBlocking { database.drop() } }
})