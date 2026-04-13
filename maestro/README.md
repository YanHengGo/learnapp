# LearnApp Maestro テスト

## 前提条件

```bash
# Maestro インストール（未インストールの場合）
curl -Ls "https://get.maestro.mobile.dev" | bash
```

## ディレクトリ構成

```
maestro/
├── .env                          # テスト用認証情報
├── flows/
│   ├── 01_auth_login.yaml        # ログイン（正常・エラー）
│   ├── 02_auth_signup.yaml       # サインアップ UI 確認
│   ├── 03_children_list.yaml     # 子ども一覧・遷移
│   ├── 04_children_crud.yaml     # 子ども 追加・編集・削除
│   ├── 05_home_navigation.yaml   # BottomNav タブ切り替え
│   ├── 06_tasks_crud.yaml        # タスク 追加・編集・アーカイブ
│   ├── 07_daily_record.yaml      # 日々の記録・チェック・保存
│   └── 09_full_flow.yaml         # E2E 全体フロー
└── subflows/
    ├── login.yaml                # ログイン共通処理
    ├── login_and_select_child.yaml  # ログイン + 子ども選択
    └── daily_with_tasks.yaml     # タスクあり時の日々記録操作
```

## 実行方法

```bash
# エミュレータ / 実機を起動した状態で実行

# 単一フロー実行
maestro test maestro/flows/01_auth_login.yaml

# 全フロー実行
maestro test maestro/flows/

# .env を明示指定する場合
maestro test --env TEST_EMAIL=demo2@example.com --env TEST_PASSWORD=pass1234 maestro/flows/01_auth_login.yaml
```

## テスト対象画面と UI 要素

| 画面 | 主な要素 |
|------|---------|
| AuthScreen | `testTag: authSubmitButton`, ラベル: メールアドレス / パスワード |
| ChildrenScreen | contentDesc: 子どもを追加 / 編集 / 削除 |
| HomeScreen | BottomNav ラベル: 日々 / タスク / 集計 |
| TasksScreen | contentDesc: タスクを追加 / 編集 / アーカイブ |
| DailyScreen | `testTag: saveButton`, contentDesc: 前の日 / 次の日 |

## 注意事項

- `04_children_crud.yaml` は「テスト太郎」「テスト二郎」を追加・削除するため、既存データには影響しません
- `06_tasks_crud.yaml` は「テスト算数」を追加・アーカイブします
- `09_full_flow.yaml` は「E2E テストタスク」を追加後にアーカイブして後片付けします
- タスクが登録されていない子どもを選択した場合、`07_daily_record.yaml` の記録操作はスキップされます
