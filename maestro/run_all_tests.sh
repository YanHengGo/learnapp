#!/bin/bash
# ============================================================
# Maestro 全テスト実行バッチ
# ============================================================
# 使用方法:
#   ./run_all_tests.sh              # デフォルト設定で実行
#   ./run_all_tests.sh --wait 10    # テスト間隔を10秒に変更
#   ./run_all_tests.sh --skip 09    # 特定フローをスキップ
#   ./run_all_tests.sh --dry-run    # 実行内容の確認のみ
# ============================================================

set -euo pipefail

# ---- 設定 ----
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
FLOWS_DIR="$SCRIPT_DIR/flows"
ENV_FILE="$SCRIPT_DIR/.env"
RESULTS_DIR="$SCRIPT_DIR/results"
LOG_FILE="$RESULTS_DIR/test_run_$(date +%Y%m%d_%H%M%S).log"

# テスト間のウェイト秒数（デフォルト: 5秒）
WAIT_BETWEEN_TESTS=5

# フロー実行後にアプリが安定するまでの追加ウェイト（デフォルト: 3秒）
WAIT_AFTER_LAUNCH=3

# スキップするフロー番号（スペース区切り、例: "02 09"）
SKIP_FLOWS=""

DRY_RUN=false

# ---- カラー出力 ----
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ---- 引数パース ----
while [[ $# -gt 0 ]]; do
  case $1 in
    --wait)
      WAIT_BETWEEN_TESTS="$2"
      shift 2
      ;;
    --skip)
      SKIP_FLOWS="$SKIP_FLOWS $2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    *)
      echo -e "${RED}不明なオプション: $1${NC}"
      echo "使用方法: $0 [--wait 秒数] [--skip フロー番号] [--dry-run]"
      exit 1
      ;;
  esac
done

# ---- ヘルパー関数 ----
log() {
  local msg="$1"
  echo "$msg" | tee -a "$LOG_FILE"
}

log_info()    { log "$(echo -e "${BLUE}[INFO]${NC}  $1")"; }
log_success() { log "$(echo -e "${GREEN}[PASS]${NC}  $1")"; }
log_error()   { log "$(echo -e "${RED}[FAIL]${NC}  $1")"; }
log_warn()    { log "$(echo -e "${YELLOW}[SKIP]${NC}  $1")"; }
log_section() { log "$(echo -e "${CYAN}$1${NC}")"; }

wait_with_countdown() {
  local secs=$1
  local label="${2:-次のテストまで待機}"
  if [[ $secs -le 0 ]]; then return; fi
  for ((i=secs; i>0; i--)); do
    printf "\r  ${YELLOW}⏳ %s: %d 秒...${NC}" "$label" "$i"
    sleep 1
  done
  printf "\r  ${GREEN}✓ 待機完了%-30s${NC}\n" ""
}

should_skip() {
  local flow_num="$1"
  for skip in $SKIP_FLOWS; do
    if [[ "$flow_num" == "$skip" ]]; then
      return 0
    fi
  done
  return 1
}

# ---- 事前チェック ----
check_prerequisites() {
  log_section "============================================================"
  log_section " 事前チェック"
  log_section "============================================================"

  # Maestro インストール確認
  if ! command -v maestro &>/dev/null; then
    log_error "maestro コマンドが見つかりません"
    log_info  "インストール: curl -Ls 'https://get.maestro.mobile.dev' | bash"
    exit 1
  fi
  log_info "maestro: $(maestro --version 2>/dev/null || echo 'バージョン取得失敗')"

  # デバイス接続確認
  if ! maestro hierarchy &>/dev/null 2>&1; then
    log_error "接続済みのデバイス/エミュレータが見つかりません"
    log_info  "エミュレータを起動するか、実機を接続してください"
    exit 1
  fi
  log_info "デバイス接続: OK"

  # flows ディレクトリ確認
  if [[ ! -d "$FLOWS_DIR" ]]; then
    log_error "flows ディレクトリが見つかりません: $FLOWS_DIR"
    exit 1
  fi

  # .env ファイル確認
  if [[ -f "$ENV_FILE" ]]; then
    log_info ".env: 検出済み"
  else
    log_warn ".env ファイルが見つかりません（環境変数で代替）"
  fi

  log_info "事前チェック完了"
  echo ""
}

# ---- メイン実行 ----
main() {
  # 結果ディレクトリ作成
  mkdir -p "$RESULTS_DIR"

  # ヘッダー表示
  log_section "============================================================"
  log_section " LearnApp Maestro 全テスト実行"
  log_section " 開始時刻: $(date '+%Y-%m-%d %H:%M:%S')"
  log_section " ログ保存: $LOG_FILE"
  log_section " テスト間隔: ${WAIT_BETWEEN_TESTS}秒"
  if [[ -n "$SKIP_FLOWS" ]]; then
    log_section " スキップ: $SKIP_FLOWS"
  fi
  log_section "============================================================"
  echo ""

  if $DRY_RUN; then
    log_warn "DRY-RUN モード: 実際のテストは実行しません"
    echo ""
  fi

  check_prerequisites

  # フロー一覧を取得（番号順）
  local flows=()
  while IFS= read -r -d '' f; do
    flows+=("$f")
  done < <(find "$FLOWS_DIR" -name "*.yaml" -print0 | sort -z)

  local total=${#flows[@]}
  local passed=0
  local failed=0
  local skipped=0
  local failed_flows=()

  log_section "============================================================"
  log_section " テスト実行（全 ${total} フロー）"
  log_section "============================================================"
  echo ""

  for i in "${!flows[@]}"; do
    local flow="${flows[$i]}"
    local basename
    basename=$(basename "$flow")
    local flow_num="${basename:0:2}"
    local index=$((i + 1))

    # スキップ判定
    if should_skip "$flow_num"; then
      log_warn "[$index/$total] $basename をスキップ"
      ((skipped++))
      continue
    fi

    log_section "------------------------------------------------------------"
    log_info "[$index/$total] $basename"
    log_info "開始: $(date '+%H:%M:%S')"

    if $DRY_RUN; then
      log_warn "DRY-RUN: maestro test \"$flow\""
      ((passed++))
    else
      # アプリ安定待ち（初回以外）
      if [[ $i -gt 0 ]]; then
        wait_with_countdown "$WAIT_AFTER_LAUNCH" "アプリ安定待ち"
      fi

      # .env を読み込んで --env KEY=VALUE 形式に変換
      local env_args=()
      if [[ -f "$ENV_FILE" ]]; then
        while IFS='=' read -r key value; do
          # 空行・コメント行をスキップ
          [[ -z "$key" || "$key" == \#* ]] && continue
          env_args+=(--env "${key}=${value}")
        done < "$ENV_FILE"
      fi

      # テスト実行
      if maestro test \
          "${env_args[@]}" \
          --format junit \
          --output "$RESULTS_DIR/${basename%.yaml}_result.xml" \
          "$flow" 2>&1 | tee -a "$LOG_FILE"; then
        log_success "[$index/$total] $basename PASSED"
        ((passed++))
      else
        log_error "[$index/$total] $basename FAILED"
        ((failed++))
        failed_flows+=("$basename")
      fi
    fi

    # テスト間ウェイト（最後のテストは不要）
    if [[ $index -lt $total ]]; then
      wait_with_countdown "$WAIT_BETWEEN_TESTS" "次のテストまで待機"
    fi

    echo ""
  done

  # ---- 結果サマリー ----
  local end_time
  end_time=$(date '+%Y-%m-%d %H:%M:%S')

  echo ""
  log_section "============================================================"
  log_section " テスト結果サマリー"
  log_section "============================================================"
  log_info "終了時刻: $end_time"
  log_info "合計:     $total フロー"
  log_success "PASSED:   $passed"
  if [[ $failed -gt 0 ]]; then
    log_error "FAILED:   $failed"
    log_error "失敗フロー:"
    for ff in "${failed_flows[@]}"; do
      log_error "  - $ff"
    done
  else
    log_info "FAILED:   0"
  fi
  if [[ $skipped -gt 0 ]]; then
    log_warn "SKIPPED:  $skipped"
  fi
  log_section "------------------------------------------------------------"
  log_info "詳細ログ:   $LOG_FILE"
  log_info "JUnit XML: $RESULTS_DIR/"
  log_section "============================================================"

  # 失敗があれば終了コード 1
  if [[ $failed -gt 0 ]]; then
    exit 1
  fi
}

main
