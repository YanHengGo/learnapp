# リリースビルド設定 設計

## 概要

Play Store 公開のための署名・難読化・最適化の設定。

---

## 1. Keystore（署名鍵）

### 作成方法

```bash
keytool -genkey -v \
  -keystore learnapp-release.jks \
  -alias learnapp \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

| 項目 | 値 |
|---|---|
| ファイル名 | `learnapp-release.jks` |
| alias | `learnapp` |
| 有効期限 | 10000日（約27年）※ Play Store 要件: 2033年10月22日以降まで有効であること |
| 保存場所 | プロジェクト外（例: `~/.android/learnapp-release.jks`）|

> **重要**: `.jks` ファイルは絶対に Git にコミットしない。紛失すると同じ applicationId でアップデート不可。

---

## 2. 署名情報の管理

### keystore.properties（プロジェクトルート）

```properties
storeFile=/Users/yourname/.android/learnapp-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=learnapp
keyPassword=YOUR_KEY_PASSWORD
```

- **`.gitignore` に追加必須**: `keystore.properties`
- ローカルにのみ保存し、チームには別途共有

---

## 3. app/build.gradle.kts の変更

### 変更前（現状）

```kotlin
buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### 変更後

```kotlin
// ファイル冒頭に追加
import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) load(keystorePropertiesFile.inputStream())
}

android {
    // ...

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 4. ProGuard ルール（proguard-rules.pro）

このプロジェクトで必要なルール:

```proguard
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

# Gson（Retrofit の JSON 変換に使用している場合）
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

# アプリの model クラス（ネットワーク DTO）
-keep class com.learn.app.core.network.** { *; }
-keep class com.learn.app.core.model.** { *; }
```

---

## 5. .gitignore への追加

```gitignore
# Keystore
*.jks
*.keystore
keystore.properties
```

---

## 6. AAB（Android App Bundle）ビルドコマンド

Play Store への提出は AAB 形式を推奨。

```bash
# AAB ビルド（Play Store 提出用）
./gradlew bundleRelease

# 出力先
app/build/outputs/bundle/release/app-release.aab

# APK ビルド（動作確認用）
./gradlew assembleRelease

# 出力先
app/build/outputs/apk/release/app-release.apk
```

---

## 7. versionCode / versionName 管理方針

| 項目 | 現在値 | ルール |
|---|---|---|
| `versionCode` | 1 | リリースごとに +1（整数、戻せない）|
| `versionName` | "1.0" | セマンティックバージョニング（例: "1.0.1", "1.1.0"）|

---

## 8. 実装手順

1. Keystore ファイルを作成（`keytool` コマンド）
2. `keystore.properties` をプロジェクトルートに作成
3. `.gitignore` に `*.jks` と `keystore.properties` を追加
4. `app/build.gradle.kts` に signingConfig を追加
5. `proguard-rules.pro` にルールを追加
6. `./gradlew bundleRelease` でビルド確認
