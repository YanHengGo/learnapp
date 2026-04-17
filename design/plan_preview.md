# @Preview 追加計画（ChildrenScreen 以外）

## 対象画面一覧

| 画面 | モジュール | `internal` Composable | ui.tooling 依存 |
|---|---|---|---|
| 認証 | `feature:auth` | `AuthContent` | ✅ 追加済み |
| スプラッシュ | `feature:splash` | `SplashContent` | ❌ 要追加 |
| ホーム | `feature:home` | `HomeContent` | ❌ 要追加 |
| タスク管理 | `feature:tasks` | `TasksContent` | ❌ 要追加 |
| 日々の記録 | `feature:daily` | `DailyContent` | ❌ 要追加 |
| 集計 | `feature:summary` | `SummaryContent` | ❌ 要追加 |

---

## 各画面のプレビューパターン

### 1. Auth（認証）

ファイル: `feature/auth/.../AuthScreenPreview.kt`

| プレビュー名 | UiState |
|---|---|
| ログイン画面 | `mode=LOGIN` |
| 新規登録画面 | `mode=SIGNUP` |
| ローディング中 | `isLoading=true` |
| エラー表示 | `errorMessage="メールアドレスまたはパスワードが正しくありません"` |
| プライバシーポリシー | `PrivacyPolicyScreen` 単体 |

### 2. Splash（スプラッシュ）

ファイル: `feature/splash/.../SplashScreenPreview.kt`

| プレビュー名 | 内容 |
|---|---|
| スプラッシュ表示 | `SplashContent()` 単体（引数なし） |

### 3. Home（ホーム）

ファイル: `feature/home/.../HomeScreenPreview.kt`

| プレビュー名 | UiState |
|---|---|
| 日々タブ選択中 | `selectedTab=DAILY`, `selectedChildName="長男"` |
| タスクタブ選択中 | `selectedTab=TASKS` |
| 集計タブ選択中 | `selectedTab=SUMMARY` |
| 子ども切り替えダイアログ | `showSwitcher=true` |
| ログアウト確認ダイアログ | `showLogoutConfirm=true` |

※ `HomeContent` の `content` ラムダには空の `Box` を渡す

### 4. Tasks（タスク管理）

ファイル: `feature/tasks/.../TasksScreenPreview.kt`

| プレビュー名 | UiState |
|---|---|
| ローディング中 | `isLoading=true` |
| タスクなし（空） | `tasks=[]` |
| タスク一覧 | タスク2件（曜日・期間あり/なし混在） |
| タスク追加ダイアログ | `showDialog=true, editingTask=null` |
| タスク編集ダイアログ | `showDialog=true, editingTask=非null` |

### 5. Daily（日々の記録）

ファイル: `feature/daily/.../DailyScreenPreview.kt`

| プレビュー名 | UiState |
|---|---|
| ローディング中 | `isLoading=true` |
| 記録なし（空） | `taskRows=[]` |
| 記録あり | `taskRows=` 2件（チェック済み/未チェック混在） |
| 保存中 | `isSaving=true` |

### 6. Summary（集計）

ファイル: `feature/summary/.../SummaryScreenPreview.kt`

| プレビュー名 | UiState |
|---|---|
| ローディング中 | `isLoading=true` |
| データあり | `calendarSummary=非null, summary=非null` |
| データなし | `calendarSummary=null` |

---

## 作業内容

### Step 1: build.gradle.kts の修正（4モジュール）

以下に `debugImplementation(libs.androidx.compose.ui.tooling)` を追加：
- `feature/splash/build.gradle.kts`
- `feature/home/build.gradle.kts`
- `feature/tasks/build.gradle.kts`
- `feature/daily/build.gradle.kts`
- `feature/summary/build.gradle.kts`

### Step 2: Preview ファイルの新規作成（6ファイル）

| ファイル | パターン数 |
|---|---|
| `AuthScreenPreview.kt` | 5 |
| `SplashScreenPreview.kt` | 1 |
| `HomeScreenPreview.kt` | 5 |
| `TasksScreenPreview.kt` | 5 |
| `DailyScreenPreview.kt` | 4 |
| `SummaryScreenPreview.kt` | 3 |

合計 **23パターン**

---

## 完了条件

- [ ] 全6モジュールでビルドが通る
- [ ] Android Studio の Preview パネルで各プレビューが表示される
- [ ] `internal` Composable を直接使用し、ViewModel に依存しない
