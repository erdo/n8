# https://github.com/Kotlin/kotlinx.serialization/blob/master/README.md#android

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class foo.bar.n8.**$$serializer { *; }
-keepclassmembers class foo.bar.example.** {
    *** Companion;
}
-keepclasseswithmembers class foo.bar.n8.** {
    kotlinx.serialization.KSerializer serializer(...);
}
