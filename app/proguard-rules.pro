# デバッグ用スタックトレースを保持
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Retrofit / OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

# Kotlin Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# DataStore
-keep class androidx.datastore.** { *; }

# アプリの model クラス・ネットワーク DTO
-keep class com.learn.app.core.network.** { *; }
-keep class com.learn.app.core.model.** { *; }
