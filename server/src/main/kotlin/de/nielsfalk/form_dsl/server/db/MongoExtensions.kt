package de.nielsfalk.form_dsl.server.db

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.BsonDocumentWrapper
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

// copied from https://github.com/nielsfalk/ktor-mongo-example
inline fun <reified T : Any> MongoDatabase.lazyGetCollection(
    collectionName: String,
    noinline initializer: (suspend MongoCollection<T>.() -> Unit)? = null
): MongoCollection<T> =
    runBlocking {
        if (listCollectionNames().filter { it == collectionName }.firstOrNull() == null) {
            createCollection(collectionName)
            initializer?.invoke(getCollection<T>(collectionName))
        }
        getCollection<T>(collectionName)
    }

suspend fun <T : Any> MongoCollection<T>.findById(id: ObjectId) =
    find(eq("_id", id)).firstOrNull()

suspend inline fun <reified T : Any> MongoCollection<T>.updateOneWithAutoVersion(
    id: ObjectId,
    entity: T
): UpdateResult {
    val bsonUpdates: List<Bson> = BsonDocumentWrapper.asBsonDocument(entity, codecRegistry)
        .filterKeys { it != "_id" }
        .map { (key, value) -> Updates.set(key, value) }
    val versionProperty = T::class.members.firstOrNull { it.name == "version" }
    return if (versionProperty == null)
        updateOne(eq("_id", id), bsonUpdates)
    else {
        val version: Long = versionProperty.call(entity) as Long
        updateOne(
            and(eq("_id", id), eq("version", version)),
            bsonUpdates + Updates.set("version", version + 1)
        )
    }
}

suspend inline fun <reified T : Any> MongoCollection<T>.updateOne(
    id: ObjectId,
    entity: T
): UpdateResult {
    val bsonUpdates: List<Bson> = BsonDocumentWrapper.asBsonDocument(entity, codecRegistry)
        .filterKeys { it != "_id" }
        .map { (key, value) -> Updates.set(key, value) }
    return updateOne(eq("_id", id), bsonUpdates)
}

val BsonValue.objectId: ObjectId
    get() = ObjectId(asObjectId().value.toHexString())