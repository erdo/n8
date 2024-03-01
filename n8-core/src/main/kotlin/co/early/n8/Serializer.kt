package co.early.n8

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedClassSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(InternalSerializationApi::class)
class NavigationSerializer<T>(valueSerializer: KSerializer<T>):
    KSerializer<Navigation<T>> {
    private val serializer = SealedClassSerializer(
        Navigation::class.simpleName!!,
        Navigation::class,
        arrayOf(
            Navigation.EndNode::class,
            Navigation.TabHost::class,
            Navigation.BackStack::class,
        ),
        arrayOf(
            Navigation.EndNode.serializer(valueSerializer),
            Navigation.TabHost.serializer(valueSerializer),
            Navigation.BackStack.serializer(valueSerializer)
        )
    )

    override val descriptor: SerialDescriptor = serializer.descriptor
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): Navigation<T> { return serializer.deserialize(decoder) as Navigation<T> }
    override fun serialize(encoder: Encoder, value: Navigation<T>) { serializer.serialize(encoder, value) }
}
