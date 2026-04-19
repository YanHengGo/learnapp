# LeakCanary 導入設計

## 概要

デバッグビルドにメモリリーク検出ライブラリ **LeakCanary** を導入する。
リリースビルドには含まれないため、ユーザーへの影響なし。

**対象**: debug ビルドのみ
**作成日**: 2026-04-19

---

## LeakCanary とは

Square 製のメモリリーク自動検出ライブラリ。

- Activity / Fragment / ViewModel / Service 等のリークを自動検出
- リーク発生時に通知 + アプリ内のリークトレーサー画面で原因を表示
- `debugImplementation` で追加するだけで動作（コード変更不要）
- リリースビルドには含まれない（依存が自動的に除外される）

---

## 変更内容

### 1. `gradle/libs.versions.toml`

```toml
[versions]
leakcanary = "2.14"   # 追加

[libraries]
leakcanary = { group = "com.squareup.leakcanary", name = "leakcanary-android", version.ref = "leakcanary" }  # 追加
```

### 2. `app/build.gradle.kts`

```kotlin
dependencies {
    // ... 既存の依存 ...
    debugImplementation(libs.leakcanary)   // 追加
}
```

---

## 修正ファイル一覧

| ファイル | 変更内容 |
|---|---|
| `gradle/libs.versions.toml` | `leakcanary` バージョン・ライブラリ定義を追加 |
| `app/build.gradle.kts` | `debugImplementation(libs.leakcanary)` を追加 |

---

## 動作確認方法

1. デバッグビルドでアプリを起動
2. メモリリークが発生した場合、通知バーに「LeakCanary: X leaks」と表示される
3. 通知タップ → アプリ内でリークのスタックトレースを確認できる

リリースビルドには LeakCanary は含まれないことを `./gradlew assembleRelease` で確認する。

---

## 注意事項

- LeakCanary は `Application.onCreate()` などへの記述不要（ContentProvider で自動初期化）
- minSdk 31 との互換性あり（LeakCanary 2.x は API 16+ 対応）
- 既存のコードへの影響なし

---

**作成日**: 2026-04-19
