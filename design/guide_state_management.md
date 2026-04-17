# Androidの状態管理: mutableStateOf vs StateFlow vs SharedFlow

Android (Jetpack Compose) 開発における ViewModel での状態管理の比較と使い分けガイド。

## 1. 比較まとめ

| 項目 | `mutableStateOf` | `StateFlow` | `SharedFlow` |
| :--- | :--- | :--- | :--- |
| **分類** | Compose 状態 (State) | Flow (状態用) | Flow (イベント用) |
| **初期値** | 必須 | 必須 | 不要 (設定による) |
| **最新値の保持** | あり | あり | なし (設定による) |
| **主な用途** | UIの単純な状態管理 | データの状態、DB/通信結果 | 一度きりの通知、画面遷移 |
| **依存関係** | Compose Runtime | Coroutines (純粋Kotlin) | Coroutines (純粋Kotlin) |

---

## 2. 各要素の詳細

### mutableStateOf (Compose State)
Composeの再描画システムと直結した状態管理。
- **メリット**: 記述が最もシンプル。Composableから直接プロパティとして参照できる。
- **デメリット**: Compose Runtimeに依存するため、純粋なKotlinモジュールやKMP（Kotlin Multiplatform）では使用できない。

### StateFlow
「現在の状態」を保持し、変化をストリームとして流す。
- **メリット**: `map`, `combine`, `debounce` などの強力な Flow 演算子が使用可能。プラットフォーム非依存。
- **用途**: 検索フィルターのリアルタイム適用や、複数のデータソースを合成する場合に真価を発揮する。
- **Composeでの利用**: `collectAsStateWithLifecycle()` を使用して購読する。

### SharedFlow
「イベント（出来事）」を通知するためのストリーム。
- **メリット**: 値を保持しないため、画面回転時などに同じイベントが再発行されるのを防げる。
- **用途**: 画面遷移、スナックバーの表示、トースト通知など。

---

## 3. 使い分けの指針

### 一律 StateFlow に寄せるメリット（推奨）
実務や大規模プロジェクトでは、以下の理由から **一律 `StateFlow`** を採用することが多い。
1. **一貫性**: すべての ViewModel が同じ構造になり、コードの読みやすさが向上する。
2. **拡張性**: 最初は単純な状態保持だけでも、後から複雑なロジック（合成や遅延実行）が必要になった際、構造を変えずに対応できる。
3. **テスト容易性**: Compose に依存せず、純粋なユニットテストでロジックを検証できる。

### 実装パターン (StateFlow)


## 4. 結論
- **シンプルさ・書き心地重視**: `mutableStateOf`
- **堅牢性・将来の拡張性重視**: `StateFlow` (プロの現場での主流)
- **一度きりのアクション**: `SharedFlow`
