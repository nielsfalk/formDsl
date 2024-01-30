package de.nielsfalk.bson.util

import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.random.Random

/**
 * A multiplatform implementation of org.bson.types.ObjectId
 */
@Serializable(with = ObjectIdSerializer::class)
data class ObjectId(
    val hexString: String
) {

    constructor(
        timestamp: UInt = Clock.System.now().epochSeconds.toUInt()
    ) : this(
        timestamp, nextCount(), ObjectId.randomValue1, ObjectId.randomValue2
    )

    private constructor(
        timestamp: UInt,
        counter: Int,
        randomValue1: Int,
        randomValue2: Short
    ) : this(
        asByteArray(timestamp, counter, randomValue1, randomValue2)
    ) {
        _timestamp = timestamp
        _randomValue1 = randomValue1
        _randomValue2 = randomValue2
        _counter = counter
    }

    private constructor(byteArray: ByteArray) : this(byteArray.toHexString()) {
        _byteArray = byteArray
    }

    private var _byteArray: ByteArray? = null
    private var _timestamp: UInt? = null
    private var _randomValue1: Int? = null
    private var _randomValue2: Short? = null
    private var _counter: Int? = null

    val byteArray by lazy {
        if (_byteArray == null) _byteArray = parseHexString(hexString)
        _byteArray!!
    }

    /** 2106 will be interpreted as 1970 but the original org.bson.types.ObjectId is worse with a signed int and 2038*/
    val timestamp: UInt by lazy {
        if (_timestamp == null) fillPropertiesFromByteArray()
        _timestamp!!
    }
    val randomValue1: Int by lazy {
        if (_randomValue1 == null) fillPropertiesFromByteArray()
        _randomValue1!!
    }
    val randomValue2: Short by lazy {
        if (_randomValue2 == null) fillPropertiesFromByteArray()
        _randomValue2!!
    }
    val counter: Int by lazy {
        if (_counter == null) fillPropertiesFromByteArray()
        _counter!!
    }


    private fun fillPropertiesFromByteArray() {
        val buffer = byteArray.iterator()
        _timestamp = makeInt(buffer.next(), buffer.next(), buffer.next(), buffer.next()).toUInt()
        _randomValue1 = makeInt(0.toByte(), buffer.next(), buffer.next(), buffer.next())
        _randomValue2 = makeShort(buffer.next(), buffer.next())
        _counter = makeInt(0.toByte(), buffer.next(), buffer.next(), buffer.next())
    }

    fun originalHashCode(): Int {
        var result: Int = timestamp.toInt()
        result = 31 * result + counter
        result = 31 * result + randomValue1
        result = 31 * result + randomValue2
        return result
    }


    // Big-Endian helpers, in this class because all other BSON numbers are little-endian
    private fun makeInt(b3: Byte, b2: Byte, b1: Byte, b0: Byte): Int =
        ((b3.toInt() shl 24) or
                ((b2.toInt() and 0xff) shl 16) or
                ((b1.toInt() and 0xff) shl 8) or
                ((b0.toInt() and 0xff)))

    private fun makeShort(b1: Byte, b0: Byte): Short =
        (((b1.toInt() and 0xff) shl 8) or ((b0.toInt() and 0xff))).toShort()

    companion object {
        private var nextCounter: Int = Random.nextInt()
        private const val LOW_ORDER_THREE_BYTES = 0x00ffffff
        fun nextCount() = (nextCounter++) and LOW_ORDER_THREE_BYTES
        val randomValue1 = Random.nextInt(0x01000000)
        val randomValue2 = Random.nextInt(0x00008000).toShort()

        fun isValid(hexString: String): Boolean =
            hexString.length == 24 &&
                    hexString.toCharArray().all { c ->
                        c in '0'..'9' ||
                                c in 'a'..'f' ||
                                c in 'A'..'F'
                    }

        fun asByteArray(
            timestamp: UInt,
            counter: Int,
            randomValue1: Int,
            randomValue2: Short
        ): ByteArray =
            byteArrayOf(
                int3(timestamp),
                int2(timestamp),
                int1(timestamp),
                int0(timestamp),
                int2(randomValue1),
                int1(randomValue1),
                int0(randomValue1),
                short1(randomValue2),
                short0(randomValue2),
                int2(counter),
                int1(counter),
                int0(counter)
            )


        private fun int3(x: UInt): Byte = (x shr 24).toByte()
        private fun int2(x: UInt): Byte = (x shr 16).toByte()
        private fun int1(x: UInt): Byte = (x shr 8).toByte()
        private fun int0(x: UInt): Byte = x.toByte()
        private fun int3(x: Int): Byte = (x shr 24).toByte()
        private fun int2(x: Int): Byte = (x shr 16).toByte()
        private fun int1(x: Int): Byte = (x shr 8).toByte()
        private fun int0(x: Int): Byte = x.toByte()
        private fun short1(x: Short): Byte = (x.toInt() shr 8).toByte()
        private fun short0(x: Short): Byte = x.toByte()
        private val hexChars = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

        private fun ByteArray.toHexString(): String =
            StringBuilder(24).apply {
                this@toHexString.forEach { byte ->
                    val i = byte.toInt()
                    append(hexChars[i shr 4 and 0x0F])
                    append(hexChars[i and 0x0F])
                }
            }
                .toString()

        private fun parseHexString(hexString: String): ByteArray =
            hexString
                .also {
                    if (it.length != 24) {
                        throw IllegalArgumentException("state should be: hexString has 24 characters")
                    }
                }
                .chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
    }
}

class ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(serialName = "ObjectId", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ObjectId =
        ObjectId(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ObjectId) {
        encoder.encodeString(value.hexString)
    }
}
