package co.early.n8

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedClassSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(InternalSerializationApi::class)
class NavigationSerializer<L, T>(locationValueSerializer: KSerializer<L>, tabHostValueSerializer: KSerializer<T>):
    KSerializer<Navigation<L, T>> {
    private val serializer = SealedClassSerializer(
        Navigation::class.simpleName!!,
        Navigation::class,
        arrayOf(
            Navigation.EndNode::class,
            Navigation.TabHost::class,
            Navigation.BackStack::class,
        ),
        arrayOf(
            Navigation.EndNode.serializer(locationValueSerializer, tabHostValueSerializer),
            Navigation.TabHost.serializer(locationValueSerializer, tabHostValueSerializer),
            Navigation.BackStack.serializer(locationValueSerializer, tabHostValueSerializer)
        )
    )

    override val descriptor: SerialDescriptor = serializer.descriptor
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): Navigation<L, T> { return serializer.deserialize(decoder) as Navigation<L, T> }
    override fun serialize(encoder: Encoder, value: Navigation<L, T>) { serializer.serialize(encoder, value) }
}
