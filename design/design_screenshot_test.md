# UIテスト スクリーンショット自動保存 — 修正点まとめ

## 概要

Compose UITest 実行後にスクリーンショットを自動保存する仕組みを追加。
adb コマンド不要で、TestStorage → Gradle タスク → ローカルフォルダへコピーする方式を採用。

---

## 修正ファイル一覧

| # | ファイル | 種別 | 内容 |
|---|----------|------|------|
| 1 | `gradle/libs.versions.toml` | 変更 | `testServices` バージョン追加・ライブラリ定義追加 |
| 2 | `.gitignore` | 変更 | `/screenshots/` を管理対象外に追加 |
| 3 | `feature/children/build.gradle.kts` | 変更 | TestStorage 設定・依存関係・Gradle タスク追加 |
| 4 | `feature/children/src/androidTest/.../ScreenshotCaptureRule.kt` | 新規 | スクリーンショット自動保存 Rule クラス |
| 5 | `feature/children/src/androidTest/.../ChildrenScreenTest.kt` | 変更 | `ScreenshotCaptureRule` を Rule として組み込み |

---

## 詳細

### 1. `gradle/libs.versions.toml`

| 項目 | 変更前 | 変更後 |
|------|--------|--------|
| バージョン定義 | （なし） | `testServices = "1.5.0"` を追加 |
| ライブラリ定義 | （なし） | `androidx-test-services` エントリを追加 |

```toml
[versions]
testServices = "1.5.0"          # 追加

[libraries]
androidx-test-services = { group = "androidx.test.services", name = "test-services", version.ref = "testServices" }  # 追加
```

---

### 2. `.gitignore`

| 項目 | 内容 |
|------|------|
| 追加行 | `/screenshots/` |
| 理由 | テスト実行のたびに生成される PNG をリポジトリに含めないため |

---

### 3. `feature/children/build.gradle.kts`

| 項目 | 変更前 | 変更後 |
|------|--------|--------|
| `testInstrumentationRunnerArguments` | （なし） | `useTestStorageService = "true"` を追加 |
| `testOptions` | 空 | `managedDevices {}` を追加（TestStorage 中継に必要） |
| 依存関係 | （なし） | `androidTestImplementation(libs.androidx.test.services)` 追加 |
| 依存関係 | （なし） | `androidTestUtil(libs.androidx.test.services)` 追加 |
| Gradle タスク | （なし） | `collectScreenshots` タスクを追加 |
| タスク連携 | （なし） | `connectedDebugAndroidTest` の `finalizedBy("collectScreenshots")` を追加 |

**`collectScreenshots` タスクの動作:**

```
build/outputs/connected_android_test_additional_output/
    └── debugAndroidTest/connected/<device>/
        └── screenshots/*.png
                ↓ コピー
screenshots/children/NNN/          ← NNN はカウントアップ（001, 002, ...）
```

---

### 4. `ScreenshotCaptureRule.kt`（新規）

| 項目 | 内容 |
|------|------|
| クラス | `ScreenshotCaptureRule : TestWatcher()` |
| トリガー | `finished()` — テスト成功・失敗にかかわらず毎回実行 |
| キャプチャ方法 | `composeRule.onRoot().captureToImage().asAndroidBitmap()` |
| 保存方法 | `TestStorage().openOutputFile(fileName)` — adb 不要 |
| ファイル名規則 | `screenshots/<ClassName>_<methodName>.png` |
| エラー処理 | `catch (_: Throwable)` — ダイアログ等でキャプチャ失敗しても無視 |

> `AssertionError` は `Exception` ではなく `Error` のサブクラスのため、
> `catch (_: Exception)` では捕捉できない → `catch (_: Throwable)` が必須。

---

### 5. `ChildrenScreenTest.kt`

| 項目 | 変更前 | 変更後 |
|------|--------|--------|
| Rule 定義 | `composeTestRule` のみ | `composeTestRule`（order=0）+ `screenshotRule`（order=1）を追加 |

```kotlin
@get:Rule(order = 0)
val composeTestRule = createComposeRule()

@get:Rule(order = 1)
val screenshotRule = ScreenshotCaptureRule(composeTestRule)
```

> `order` を明示しないと Rule の実行順序が不定になり、
> `screenshotRule` が `composeTestRule` より外側に配置されてキャプチャが失敗する。

---

## キャプチャの流れ

### デバイス側（テスト実行中）

```
ChildrenScreenTest
  │
  │ @get:Rule(order=1)
  ▼
ScreenshotCaptureRule.finished()
  │
  │ composeRule.onRoot().captureToImage()
  │ → Bitmap 生成
  │
  │ TestStorage().openOutputFile("screenshots/xxx.png")
  │ → AndroidTestStorageService 経由で書き込み
  ▼
デバイス内の特殊領域（TestStorage管理）
```

### ホスト側（テスト完了後）

```
connectedDebugAndroidTest 完了
  │ finalizedBy
  ▼
collectScreenshots タスク（build.gradle.kts）
  │
  │ build/outputs/connected_android_test_additional_output/
  │ .../<device>/ 配下の PNG を走査
  │
  │ nextNum = 既存フォルダ最大値 + 1
  ▼
~/dev/learnapp/screenshots/children/001/*.png
```

---

## build.gradle.kts の各設定の役割

| 設定 | 役割 |
|------|------|
| `useTestStorageService = "true"` | TestStorage API を有効化。これがないと `openOutputFile()` が動かない |
| `androidTestUtil(test-services)` | `AndroidTestStorageService.apk` をデバイスにインストール。TestStorage の受け口 |
| `androidTestImplementation(test-services)` | テストコードから `TestStorage` クラスを参照するため |
| `managedDevices {}` | Gradle が TestStorage の出力を `build/outputs/` へ中継する仕組みを有効化 |
| `collectScreenshots` タスク | `build/outputs/` にコピーされた PNG をカウントアップフォルダへ移動 |
| `finalizedBy("collectScreenshots")` | テスト完了後に自動実行。手動 `adb pull` が不要になる |

**役割の分担:**
- `ScreenshotCaptureRule` → **デバイス側**でキャプチャして TestStorage に渡す
- `build.gradle.kts` → **ホスト側**で受け取って整理する

---

## 実行方法

```bash
# エミュレーターのみ指定して実行（実機は test-services インストール失敗の場合あり）
ANDROID_SERIAL=emulator-5554 ./gradlew :feature:children:connectedAndroidTest

# 手動でスクリーンショットだけ収集したい場合
./gradlew :feature:children:collectScreenshots
```

## 保存先

```
~/dev/learnapp/screenshots/children/
    ├── 001/
    │   ├── ChildrenScreenTest_loading_showsProgressIndicator.png
    │   ├── ChildrenScreenTest_emptyChildren_showsEmptyMessage.png
    │   └── ...
    ├── 002/    ← 2回目実行時に追加
    └── ...
```
