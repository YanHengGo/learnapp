# Core PKG 設計書

## 概要

4つのモジュールの責務と、全エンドポイントを実現するためのクラス設計。

```
core:model      ドメインモデル（アプリ内部で使う型）
core:network    APIとの通信（Retrofit DTO・インターフェース）
core:datastore  ローカル永続化（JWT保存）
core:data       Repository実装・DTO→モデル変換
```

依存方向：

```
core:data → core:network
core:data → core:domain   (Repositoryインターフェース)
core:data → core:datastore
core:data → core:model
core:network → core:model
core:datastore → (なし)
core:model → (なし)
```

---

## core:model

アプリ内部で使うドメインモデル。APIの形式に依存しない。

```
com.learn.app.core.model/
├── User.kt
├── Child.kt
├── Task.kt
├── DailyView.kt
├── DailyTask.kt
├── DailyItem.kt
├── CalendarSummary.kt
├── CalendarDay.kt
├── CalendarStatus.kt
└── Summary.kt
```

### User.kt
```kotlin
data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val provider: String,       // "password" | "google" | "github"
)
```

### Child.kt
```kotlin
data class Child(
    val id: String,
    val name: String,
    val grade: String?,
    val isActive: Boolean,
)
```

### Task.kt
```kotlin
data class Task(
    val id: String,
    val name: String,
    val description: String?,   // ⚠️ 現在のコードに未定義
    val subject: String,
    val defaultMinutes: Int,
    val daysMask: Int,          // 7bitフラグ（日〜土）
    val isArchived: Boolean,
    val startDate: String?,     // "YYYY-MM-DD"
    val endDate: String?,       // "YYYY-MM-DD"
)
```

> ⚠️ `childId` / `sortOrder` はAPIレスポンスに含まれないため除外。

### DailyView.kt  ← 新規
`GET /daily-view` のレスポンス全体。

```kotlin
data class DailyView(
    val date: String,           // "YYYY-MM-DD"
    val weekday: String,        // "月" | "火" | ... | "日"
    val tasks: List<DailyTask>,
)
```

### DailyTask.kt  ← 新規
`daily-view` 内の1タスク分。

```kotlin
data class DailyTask(
    val taskId: String,
    val name: String,
    val subject: String,
    val defaultMinutes: Int,
    val daysMask: Int,
    val isDone: Boolean,
    val minutes: Int,           // isDone=false のとき defaultMinutes と同値
)
```

### DailyItem.kt  ← 新規
`GET /daily` のアイテム、および `PUT /daily` の送信単位。

```kotlin
data class DailyItem(
    val taskId: String,
    val minutes: Int,
)
```

### CalendarSummary.kt  ← 新規
`GET /calendar-summary` のレスポンス全体。

```kotlin
data class CalendarSummary(
    val from: String,
    val to: String,
    val days: List<CalendarDay>,
)
```

### CalendarDay.kt  ← 新規
```kotlin
data class CalendarDay(
    val date: String,
    val status: CalendarStatus,
    val total: Int,
    val done: Int,
)
```

### CalendarStatus.kt  ← 新規
```kotlin
enum class CalendarStatus {
    GREEN,   // 全タスク完了
    YELLOW,  // 一部完了
    RED,     // 未完了
    WHITE,   // 未来日またはタスクなし
}
```

### Summary.kt  ← 新規
`GET /summary` のレスポンス全体。

```kotlin
data class Summary(
    val from: String,
    val to: String,
    val totalMinutes: Int,
    val byDay: List<SummaryByDay>,
    val bySubject: List<SummaryBySubject>,
    val byTask: List<SummaryByTask>,
)

data class SummaryByDay(
    val date: String,
    val minutes: Int,
)

data class SummaryBySubject(
    val subject: String,
    val minutes: Int,
)

data class SummaryByTask(
    val taskId: String,
    val name: String,
    val subject: String,
    val minutes: Int,
)
```

---

## core:network

Retrofit のインターフェース・DTO・DI設定。

```
com.learn.app.core.network/
├── LearnApiService.kt
├── request/
│   ├── AuthRequest.kt
│   ├── ChildRequest.kt
│   ├── TaskRequest.kt
│   └── DailyRequest.kt
├── response/
│   ├── AuthDto.kt
│   ├── ChildDto.kt
│   ├── TaskDto.kt
│   ├── DailyDto.kt
│   └── SummaryDto.kt
└── di/
    ├── NetworkModule.kt
    └── AuthInterceptor.kt
```

### request/AuthRequest.kt
```kotlin
data class LoginRequest(
    val email: String,
    val password: String,
)

data class SignupRequest(
    val email: String,
    val password: String,
)
```

### request/ChildRequest.kt
```kotlin
data class CreateChildRequest(
    val name: String,
    val grade: String?,
)

data class UpdateChildRequest(
    val name: String,
    val grade: String?,
)

// PATCH用：フィールドを個別に送るためMapを使う
// Map<String, Any?> をそのままBodyに渡す
```

### request/TaskRequest.kt
```kotlin
data class CreateTaskRequest(
    val name: String,
    val description: String?,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

data class UpdateTaskRequest(
    val name: String,
    val description: String?,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("is_archived") val isArchived: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

data class ReorderRequest(
    val orders: List<ReorderItem>,
)

data class ReorderItem(
    @SerializedName("task_id") val taskId: String,
    @SerializedName("sort_order") val sortOrder: Int,
)
```

### request/DailyRequest.kt
```kotlin
data class UpdateDailyRequest(
    val items: List<DailyItemRequest>,      // ⚠️ "logs" ではなく "items"
)

data class DailyItemRequest(
    @SerializedName("task_id") val taskId: String,
    val minutes: Int,
)
```

### response/AuthDto.kt
```kotlin
data class TokenDto(
    val token: String,
)

data class SignupDto(                        // ⚠️ signup は token でなく user を返す
    val user: SignupUserDto,
)

data class SignupUserDto(
    val id: String,
    val email: String,
)

data class MeDto(
    val user: UserDto,
)

data class UserDto(
    val id: String,
    val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val provider: String,
)
```

### response/ChildDto.kt
```kotlin
data class ChildDto(
    val id: String,
    val name: String,
    val grade: String?,
    @SerializedName("is_active") val isActive: Boolean,
)

// ⚠️ GET /children は List<ChildDto> を直接返す（ラッパーなし）
```

### response/TaskDto.kt
```kotlin
data class TaskDto(
    val id: String,
    val name: String,
    val description: String?,               // ⚠️ 現在のコードに未定義
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("is_archived") val isArchived: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

// ⚠️ GET /tasks は List<TaskDto> を直接返す（ラッパーなし）
// ⚠️ sort_order / child_id はレスポンスに含まれない
```

### response/DailyDto.kt
```kotlin
data class DailyViewDto(
    val date: String,
    val weekday: String,
    val tasks: List<DailyTaskDto>,
)

data class DailyTaskDto(
    @SerializedName("task_id") val taskId: String,
    val name: String,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("is_done") val isDone: Boolean,
    val minutes: Int,
)

data class DailyLogDto(
    val date: String,
    val items: List<DailyItemDto>,          // ⚠️ "logs" ではなく "items"
)

data class DailyItemDto(
    @SerializedName("task_id") val taskId: String,
    val minutes: Int,
)

data class UpdateDailyResponseDto(
    val date: String,
    @SerializedName("saved_count") val savedCount: Int,
)
```

### response/SummaryDto.kt
```kotlin
data class CalendarSummaryDto(
    val from: String,
    val to: String,
    val days: List<CalendarDayDto>,
)

data class CalendarDayDto(
    val date: String,
    val status: String,                     // "green" | "yellow" | "red" | "white"
    val total: Int,
    val done: Int,
)

data class SummaryDto(
    val from: String,
    val to: String,
    @SerializedName("total_minutes") val totalMinutes: Int,
    @SerializedName("by_day") val byDay: List<SummaryByDayDto>,
    @SerializedName("by_subject") val bySubject: List<SummaryBySubjectDto>,
    @SerializedName("by_task") val byTask: List<SummaryByTaskDto>,
)

data class SummaryByDayDto(
    @SerializedName("date") val date: String,
    val minutes: Int,
)

data class SummaryBySubjectDto(
    val subject: String,
    val minutes: Int,
)

data class SummaryByTaskDto(
    @SerializedName("task_id") val taskId: String,
    val name: String,
    val subject: String,
    val minutes: Int,
)
```

### LearnApiService.kt（修正後）

```kotlin
interface LearnApiService {

    // Auth
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body body: SignupRequest): SignupDto

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): TokenDto

    @GET("api/v1/me")
    suspend fun getMe(): MeDto

    // Children
    @GET("api/v1/children")
    suspend fun getChildren(): List<ChildDto>                       // ⚠️ ラッパーなし

    @POST("api/v1/children")
    suspend fun createChild(@Body body: CreateChildRequest): ChildDto

    @PATCH("api/v1/children/{childId}")
    suspend fun patchChild(
        @Path("childId") childId: String,
        @Body body: Map<String, Any?>,
    ): ChildDto

    @PUT("api/v1/children/{id}")
    suspend fun updateChild(
        @Path("id") id: String,
        @Body body: UpdateChildRequest,
    ): ChildDto

    @DELETE("api/v1/children/{childId}")
    suspend fun deleteChild(@Path("childId") childId: String)

    // Tasks
    @GET("api/v1/children/{childId}/tasks")
    suspend fun getTasks(
        @Path("childId") childId: String,
        @Query("archived") archived: Boolean = false,
    ): List<TaskDto>                                                // ⚠️ ラッパーなし

    @POST("api/v1/children/{childId}/tasks")
    suspend fun createTask(
        @Path("childId") childId: String,
        @Body body: CreateTaskRequest,
    ): TaskDto

    @PUT("api/v1/children/{childId}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("childId") childId: String,
        @Path("taskId") taskId: String,
        @Body body: UpdateTaskRequest,
    ): TaskDto

    @PUT("api/v1/children/{childId}/tasks/reorder")
    suspend fun reorderTasks(
        @Path("childId") childId: String,
        @Body body: ReorderRequest,                                 // ⚠️ {orders:[{task_id, sort_order}]}
    )

    @PATCH("api/v1/tasks/{taskId}")
    suspend fun patchTask(
        @Path("taskId") taskId: String,
        @Body body: Map<String, Any?>,
    ): TaskDto

    // Daily
    @GET("api/v1/children/{childId}/daily-view")
    suspend fun getDailyView(
        @Path("childId") childId: String,
        @Query("date") date: String,
    ): DailyViewDto

    @GET("api/v1/children/{childId}/daily")
    suspend fun getDailyLog(
        @Path("childId") childId: String,
        @Query("date") date: String,
    ): DailyLogDto

    @PUT("api/v1/children/{childId}/daily")
    suspend fun updateDailyLog(
        @Path("childId") childId: String,
        @Query("date") date: String,
        @Body body: UpdateDailyRequest,                            // ⚠️ {items:[...]}
    ): UpdateDailyResponseDto

    // Summary
    @GET("api/v1/children/{childId}/calendar-summary")
    suspend fun getCalendarSummary(
        @Path("childId") childId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): CalendarSummaryDto

    @GET("api/v1/children/{childId}/summary")
    suspend fun getSummary(
        @Path("childId") childId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): SummaryDto
}
```

---

## core:datastore

JWTトークンのみ管理。変更なし。

```
com.learn.app.core.datastore/
└── TokenDataStore.kt
```

```kotlin
class TokenDataStore @Inject constructor(@ApplicationContext context: Context) {
    val token: Flow<String?>        // JWTトークンを読み取る
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
```

---

## core:data

Repository実装とDTO→モデル変換（Mapper）。

```
com.learn.app.core.data/
├── repository/
│   ├── AuthRepositoryImpl.kt
│   ├── ChildrenRepositoryImpl.kt
│   ├── TaskRepositoryImpl.kt
│   ├── DailyRepositoryImpl.kt      ← 新規（旧StudyLogRepositoryImpl）
│   └── SummaryRepositoryImpl.kt    ← 新規
├── mapper/
│   ├── ChildMapper.kt
│   ├── TaskMapper.kt
│   ├── DailyMapper.kt              ← 新規
│   └── SummaryMapper.kt            ← 新規
└── di/
    └── DataModule.kt
```

### Repositoryと対応するAPIエンドポイント

| Repository | メソッド | API |
|---|---|---|
| `AuthRepositoryImpl` | `login()` | POST /auth/login |
| | `signup()` | POST /auth/signup |
| | `getMe()` | GET /me |
| | `logout()` | DataStore clearToken のみ |
| `ChildrenRepositoryImpl` | `getChildren()` | GET /children |
| | `createChild()` | POST /children |
| | `updateChild()` | PUT /children/:id |
| | `patchChild()` | PATCH /children/:childId |
| | `deleteChild()` | DELETE /children/:childId |
| `TaskRepositoryImpl` | `getTasks()` | GET /tasks |
| | `createTask()` | POST /tasks |
| | `updateTask()` | PUT /tasks/:taskId |
| | `archiveTask()` | PATCH /tasks/:taskId |
| | `reorderTasks()` | PUT /tasks/reorder |
| `DailyRepositoryImpl` | `getDailyView()` | GET /daily-view |
| | `getDailyLog()` | GET /daily |
| | `updateDailyLog()` | PUT /daily |
| `SummaryRepositoryImpl` | `getCalendarSummary()` | GET /calendar-summary |
| | `getSummary()` | GET /summary |

### Mapper 設計

各 `mapper/` ファイルはDTO→モデルの変換関数を持つ。

```kotlin
// ChildMapper.kt
fun ChildDto.toModel(): Child

// TaskMapper.kt
fun TaskDto.toModel(): Task

// DailyMapper.kt
fun DailyViewDto.toModel(): DailyView
fun DailyTaskDto.toModel(): DailyTask
fun DailyLogDto.toModel(): List<DailyItem>

// SummaryMapper.kt
fun CalendarSummaryDto.toModel(): CalendarSummary
fun CalendarDayDto.toModel(): CalendarDay
fun String.toCalendarStatus(): CalendarStatus   // "green" → CalendarStatus.GREEN
fun SummaryDto.toModel(): Summary
```

---

## 現在のコードから変更が必要な箇所

| ファイル | 変更内容 |
|---|---|
| `core/model/Task.kt` | `description: String?` 追加、`childId` / `sortOrder` 削除 |
| `core/model/` | `DailyView`, `DailyTask`, `DailyItem`, `CalendarSummary`, `CalendarDay`, `CalendarStatus`, `Summary` 追加 |
| `core/network/NetworkResponse.kt` | 全クラスを `response/` 配下に分割・修正 |
| `core/network/LearnApiService.kt` | 全メソッドのリクエスト・レスポンス型を修正 |
| `core/data/repository/StudyLogRepositoryImpl.kt` | `DailyRepositoryImpl` に改名・修正 |
| `core/data/repository/` | `SummaryRepositoryImpl` 追加 |
| `core/data/mapper/` | Mapper クラスを新規追加 |
| `core/domain/repository/StudyLogRepository.kt` | `DailyRepository` に改名・メソッド追加 |
| `core/domain/repository/` | `SummaryRepository` interface 追加 |
| `core/data/di/DataModule.kt` | 新規Repository のBinding追加 |
