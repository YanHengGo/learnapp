# プライバシーポリシー画面 実装計画

## 要件まとめ

| 項目 | 内容 |
|---|---|
| 開発者名 | ゲンコウソフトウェア |
| 連絡先 | genmanabu@gmail.com |
| 掲載場所 | アプリ内画面 |
| 対象地域 | 日本のみ |
| 広告・分析SDK | なし |
| アプリ名 | LearnApp |

---

## 注意事項

Google Play Console のストア登録には**WebページのURL**が必要です。
アプリ内実装に加えて、将来的に GitHub Pages や Notion 等での公開を推奨します。
（現フェーズはアプリ内実装のみ対応）

---

## 実装方針

### モジュール配置

新規モジュールは作らず、`feature/auth` に追加する。
（サインアップ画面からリンクする用途が主なため）

### 画面構成

```
PrivacyPolicyScreen
  └── Scaffold
        ├── TopAppBar（「プライバシーポリシー」+ 戻るボタン）
        └── LazyColumn（ポリシー本文）
```

### ナビゲーション追加箇所

1. **サインアップ画面**（AuthScreen）に「プライバシーポリシー」リンクを追加
2. **NavGraph** に `privacy_policy` ルートを追加

---

## 実装ステップ

### Step 1: プライバシーポリシー本文の作成
- `feature/auth/src/main/res/raw/privacy_policy.txt` に本文を配置
- 内容（下記「ポリシー構成」参照）

### Step 2: PrivacyPolicyScreen の作成
- `feature/auth/src/main/java/com/learn/app/feature/auth/PrivacyPolicyScreen.kt`
- シンプルな静的テキスト表示画面

### Step 3: NavGraph にルート追加
- `app/src/main/java/com/learn/app/NavGraph.kt` に `privacy_policy` ルートを追加

### Step 4: AuthScreen にリンク追加
- サインアップフォームの下部に「プライバシーポリシーを確認する」テキストリンクを追加

---

## ポリシー本文の構成

1. **はじめに** — アプリ概要・本ポリシーの目的
2. **収集する情報** — メールアドレス・子ども情報（名前・学年）・学習記録
3. **情報の利用目的** — サービス提供・機能改善のみ
4. **第三者への提供** — 提供しない（広告・分析SDK なし）
5. **情報の管理** — サーバー上での保管・セキュリティ
6. **お問い合わせ** — genmanabu@gmail.com
7. **改定について** — 変更時はアプリ内で通知
8. **制定日** — 2026年4月16日

---

## 変更ファイル一覧

| ファイル | 変更種別 |
|---|---|
| `feature/auth/src/main/java/.../PrivacyPolicyScreen.kt` | 新規作成 |
| `feature/auth/src/main/res/raw/privacy_policy.txt` | 新規作成 |
| `feature/auth/src/main/java/.../AuthScreen.kt` | 修正（リンク追加） |
| `app/src/main/java/com/learn/app/NavGraph.kt` | 修正（ルート追加） |

---

## 完了条件

- [ ] サインアップ画面からプライバシーポリシー画面に遷移できる
- [ ] 戻るボタンでサインアップ画面に戻れる
- [ ] ポリシー本文が全項目表示される
- [ ] ビルドが通る
