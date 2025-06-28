package com.pivoinescapano.identifier.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

@OptIn(ExperimentalSerializationApi::class)
object ImageFieldSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ImageField", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: String?,
    ) {
        encoder.encodeString(value ?: "")
    }

    override fun deserialize(decoder: Decoder): String? {
        return when (val element = (decoder as JsonDecoder).decodeJsonElement()) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.content == "false" -> null
                    else -> element.content
                }
            }

            else -> null
        }
    }
}

@Serializable
data class PeonyInfo(
    val id: Int,
    val cultivar: String,
    val originator: String,
    val date: String,
    val group: String,
    val reference: String? = null,
    val country: String? = null,
    val description: String,
    @Serializable(with = ImageFieldSerializer::class)
    val image: String?,
    val url: String,
)
