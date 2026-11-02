# Keep Data Transfer Objects (DTOs), Entities, and Models for Gson/Room serialization
-keep class com.example.selfevo.data.remote.dto.** { *; }
-keep class com.example.selfevo.data.db.entity.** { *; }
-keep class com.example.selfevo.data.model.** { *; }

# Keep Retrofit service interfaces
-keep interface com.example.selfevo.data.remote.SelfEvoApiService { *; }

# Gson specific rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
