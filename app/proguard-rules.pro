# Keep Room entities
-keep class com.example.data.local.** { *; }

# Keep Retrofit API interfaces
-keep class com.example.data.remote.** { *; }

# Keep Moshi adapters
-keepattributes Signature
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @com.squareup.moshi.JsonQualifier <methods>;
}

# Keep Firebase classes
-keep class com.google.firebase.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Room runtime support
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public void clearAllTables();
    <methods>;
}

# Retrofit & OkHttp support
-keepattributes InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-keepclassmembers,allowshrinking,allowoptimization interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Moshi support
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
-keep @com.squareup.moshi.JsonClass class * { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

# Optimize & Shrink Material Icons
-dontwarn androidx.compose.material.icons.**

# R8 Code Optimization
-optimizationpasses 5
-allowaccessmodification
