# Learn App API ドキュメント

**Base URL:** `https://ts-memo-api-1.onrender.com`

認証が必要なエンドポイントは `Authorization: Bearer <token>` ヘッダーが必要です。

---

## 認証

### POST /api/v1/auth/signup

新規ユーザー登録。トークンは返さず、ユーザー情報のみ返す。
登録後はログインAPIでトークンを取得する。

**Request**
```json
POST /api/v1/auth/signup
Content-Type: application/json

{
  "email": "demo2@example.com",
  "password": "pass1234"
}
```

**Response** `201 Created`
```json
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "demo2@example.com",
    "created_at": "2025-01-01T00:00:00.000Z",
    "updated_at": "2025-01-01T00:00:00.000Z"
  }
}
```

**エラー**
| ステータス | エラー |
|---|---|
| 400 | `email and password are required` |
| 409 | `email already exists` |

---

### POST /api/v1/auth/login

メールアドレスとパスワードでログイン。JWTトークンを返す。

**Request**
```json
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "demo2@example.com",
  "password": "pass1234"
}
```

**Response** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**エラー**
| ステータス | エラー |
|---|---|
| 400 | `email and password are required` |
| 401 | `invalid credentials` |

---

### GET /api/v1/me

🔒 認証必須
ログイン中のユーザー情報を取得する。

**Request**
```
GET /api/v1/me
Authorization: Bearer <token>
```

**Response** `200 OK`
```json
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "demo2@example.com",
    "display_name": "山田太郎",
    "avatar_url": "https://example.com/avatar.png",
    "provider": "password"
  }
}
```

---

## 子ども管理

### GET /api/v1/children

🔒 認証必須
ログインユーザーの子ども一覧を取得する（`is_active = true` のみ）。

**Request**
```
GET /api/v1/children
Authorization: Bearer <token>
```

**Response** `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "太郎",
    "grade": "小学2年"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "花子",
    "grade": null
  }
]
```

---

### POST /api/v1/children

🔒 認証必須
子どもを新規作成する。

**Request**
```json
POST /api/v1/children
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "太郎",
  "grade": "小学2年"
}
```

> `grade` は省略可能。

**Response** `201 Created`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "太郎",
  "grade": "小学2年",
  "is_active": true
}
```

---

### PATCH /api/v1/children/:childId

🔒 認証必須
子どもの情報を部分更新する。変更したいフィールドのみ送信。

**Request**
```json
PATCH /api/v1/children/550e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "太郎",
  "grade": "小学3年",
  "is_active": true
}
```

**Response** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "太郎",
  "grade": "小学3年",
  "is_active": true
}
```

---

### PUT /api/v1/children/:id

🔒 認証必須
子どもの情報を全フィールド更新する（`name` は必須）。

**Request**
```json
PUT /api/v1/children/550e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "太郎",
  "grade": "小学3年"
}
```

**Response** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "太郎",
  "grade": "小学3年",
  "is_active": true
}
```

---

### DELETE /api/v1/children/:childId

🔒 認証必須
子どもを論理削除する（`is_active = false` に更新）。

**Request**
```
DELETE /api/v1/children/550e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
```

**Response** `204 No Content`

---

## タスク管理

### GET /api/v1/children/:childId/tasks

🔒 認証必須
子どものタスク一覧を取得する。`archived` クエリで表示切り替え。

**Request**
```
GET /api/v1/children/550e8400-e29b-41d4-a716-446655440001/tasks?archived=false
Authorization: Bearer <token>
```

> `archived` は `true` / `false`（省略時は `false`）。

**Response** `200 OK`
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "name": "算数ドリル",
    "description": "教科書p.10-15",
    "subject": "算数",
    "default_minutes": 30,
    "days_mask": 62,
    "is_archived": false,
    "start_date": "2025-04-01",
    "end_date": null
  }
]
```

> `days_mask` は月〜日の7ビットフラグ（例: `62` = 月〜金）。

---

### POST /api/v1/children/:childId/tasks

🔒 認証必須
タスクを新規作成する。

**Request**
```json
POST /api/v1/children/550e8400-e29b-41d4-a716-446655440001/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "算数ドリル",
  "description": "教科書p.10-15",
  "subject": "算数",
  "default_minutes": 30,
  "days_mask": 62,
  "start_date": "2025-04-01",
  "end_date": null
}
```

> `description` / `start_date` / `end_date` は省略可能。
> `default_minutes` は省略時 `15`。

**Response** `201 Created`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "算数ドリル",
  "description": "教科書p.10-15",
  "subject": "算数",
  "default_minutes": 30,
  "days_mask": 62,
  "is_archived": false,
  "start_date": "2025-04-01",
  "end_date": null
}
```

---

### PUT /api/v1/children/:childId/tasks/:taskId

🔒 認証必須
タスクを全フィールド更新する（全フィールド必須）。

**Request**
```json
PUT /api/v1/children/550e8400-e29b-41d4-a716-446655440001/tasks/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "算数ドリル",
  "description": "教科書p.20-25",
  "subject": "算数",
  "default_minutes": 45,
  "days_mask": 62,
  "is_archived": false,
  "start_date": "2025-04-01",
  "end_date": "2025-06-30"
}
```

**Response** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "算数ドリル",
  "description": "教科書p.20-25",
  "subject": "算数",
  "default_minutes": 45,
  "days_mask": 62,
  "is_archived": false,
  "start_date": "2025-04-01",
  "end_date": "2025-06-30"
}
```

---

### PUT /api/v1/children/:childId/tasks/reorder

🔒 認証必須
タスクの並び順を一括更新する。

**Request**
```json
PUT /api/v1/children/550e8400-e29b-41d4-a716-446655440001/tasks/reorder
Authorization: Bearer <token>
Content-Type: application/json

{
  "orders": [
    { "task_id": "660e8400-e29b-41d4-a716-446655440001", "sort_order": 0 },
    { "task_id": "660e8400-e29b-41d4-a716-446655440002", "sort_order": 1 },
    { "task_id": "660e8400-e29b-41d4-a716-446655440003", "sort_order": 2 }
  ]
}
```

**Response** `200 OK`
```json
{
  "updated": 3
}
```

---

### PATCH /api/v1/tasks/:taskId

🔒 認証必須
タスクを部分更新する（変更フィールドのみ送信可）。アーカイブ操作に主に使用。

**Request**
```json
PATCH /api/v1/tasks/660e8400-e29b-41d4-a716-446655440001
Authorization: Bearer <token>
Content-Type: application/json

{
  "is_archived": true
}
```

**Response** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "算数ドリル",
  "description": null,
  "subject": "算数",
  "default_minutes": 30,
  "days_mask": 62,
  "is_archived": true,
  "start_date": null,
  "end_date": null
}
```

---

## 学習ログ

### GET /api/v1/children/:childId/daily-view

🔒 認証必須
指定日のタスク一覧と学習ログをまとめて取得する（メイン画面用）。
その日の曜日に該当するタスクのみ返す。

**Request**
```
GET /api/v1/children/550e8400-e29b-41d4-a716-446655440001/daily-view?date=2025-04-10
Authorization: Bearer <token>
```

**Response** `200 OK`
```json
{
  "date": "2025-04-10",
  "weekday": "木",
  "tasks": [
    {
      "task_id": "660e8400-e29b-41d4-a716-446655440001",
      "name": "算数ドリル",
      "subject": "算数",
      "default_minutes": 30,
      "days_mask": 62,
      "is_done": true,
      "minutes": 35
    },
    {
      "task_id": "660e8400-e29b-41d4-a716-446655440002",
      "name": "漢字練習",
      "subject": "国語",
      "default_minutes": 20,
      "days_mask": 62,
      "is_done": false,
      "minutes": 20
    }
  ]
}
```

> `is_done: false` の場合 `minutes` は `default_minutes` の値。

---

### GET /api/v1/children/:childId/daily

🔒 認証必須
指定日の学習ログ（記録済みデータ）のみ取得する。

**Request**
```
GET /api/v1/children/550e8400-e29b-41d4-a716-446655440001/daily?date=2025-04-10
Authorization: Bearer <token>
```

**Response** `200 OK`
```json
{
  "date": "2025-04-10",
  "items": [
    { "task_id": "660e8400-e29b-41d4-a716-446655440001", "minutes": 35 }
  ]
}
```

---

### PUT /api/v1/children/:childId/daily

🔒 認証必須
指定日の学習ログを一括保存する（既存データを全て上書き）。
`items` を空配列にすると当日のログが全削除される。

**Request**
```json
PUT /api/v1/children/550e8400-e29b-41d4-a716-446655440001/daily?date=2025-04-10
Authorization: Bearer <token>
Content-Type: application/json

{
  "items": [
    { "task_id": "660e8400-e29b-41d4-a716-446655440001", "minutes": 35 },
    { "task_id": "660e8400-e29b-41d4-a716-446655440002", "minutes": 25 }
  ]
}
```

> `minutes` は1以上の整数。同一 `task_id` の重複は不可。

**Response** `200 OK`
```json
{
  "date": "2025-04-10",
  "saved_count": 2
}
```

---

## 集計・統計

### GET /api/v1/children/:childId/calendar-summary

🔒 認証必須
指定期間の日別達成状況をカレンダー表示用に取得する（最大62日）。

**Request**
```
GET /api/v1/children/550e8400-e29b-41d4-a716-446655440001/calendar-summary?from=2025-04-01&to=2025-04-30
Authorization: Bearer <token>
```

**Response** `200 OK`
```json
{
  "from": "2025-04-01",
  "to": "2025-04-30",
  "days": [
    { "date": "2025-04-01", "status": "green", "total": 3, "done": 3 },
    { "date": "2025-04-02", "status": "yellow", "total": 3, "done": 1 },
    { "date": "2025-04-03", "status": "red",    "total": 2, "done": 0 },
    { "date": "2025-04-04", "status": "white",  "total": 0, "done": 0 },
    { "date": "2025-04-10", "status": "white",  "total": 2, "done": 0 }
  ]
}
```

> **status の意味**
> - `green` : 全タスク完了
> - `yellow`: 一部完了
> - `red`   : 未完了（タスクあり）
> - `white` : 未来日、またはその日のタスクなし

---

### GET /api/v1/children/:childId/summary

🔒 認証必須
指定期間の学習時間を集計する（最大366日）。

**Request**
```
GET /api/v1/children/550e8400-e29b-41d4-a716-446655440001/summary?from=2025-04-01&to=2025-04-30
Authorization: Bearer <token>
```

**Response** `200 OK`
```json
{
  "from": "2025-04-01",
  "to": "2025-04-30",
  "total_minutes": 420,
  "by_day": [
    { "date": "2025-04-01", "minutes": 55 },
    { "date": "2025-04-02", "minutes": 35 }
  ],
  "by_subject": [
    { "subject": "算数", "minutes": 200 },
    { "subject": "国語", "minutes": 150 },
    { "subject": "英語", "minutes": 70 }
  ],
  "by_task": [
    { "task_id": "660e8400-e29b-41d4-a716-446655440001", "name": "算数ドリル", "subject": "算数", "minutes": 200 },
    { "task_id": "660e8400-e29b-41d4-a716-446655440002", "name": "漢字練習",   "subject": "国語", "minutes": 150 }
  ]
}
```

---

## days_mask ビットフラグ仕様

タスクの実施曜日は7ビットのビットマスクで表現します（日曜始まり）。

| ビット | 値 | 曜日 |
|---|---|---|
| bit 0 | 1 | 日 |
| bit 1 | 2 | 月 |
| bit 2 | 4 | 火 |
| bit 3 | 8 | 水 |
| bit 4 | 16 | 木 |
| bit 5 | 32 | 金 |
| bit 6 | 64 | 土 |

**例**
| days_mask | 実施曜日 |
|---|---|
| `62` | 月〜金（2+4+8+16+32） |
| `127` | 毎日（1+2+4+8+16+32+64） |
| `65` | 土・日（1+64） |
