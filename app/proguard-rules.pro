# AuraPlayer Proguard / R8 Optimization Rules

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class * {
    <fields>;
}

# Media3 & ExoPlayer
-keep class androidx.media3.common.** { *; }
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.ui.** { *; }
-keep class androidx.media3.session.** { *; }
-dontwarn androidx.media3.**

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# Jetpack Compose
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.material3.** { *; }

# AudioFx (Equalizer, BassBoost, Virtualizer)
-keep class android.media.audiofx.** { *; }
