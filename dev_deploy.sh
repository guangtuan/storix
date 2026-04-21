#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: ./dev_deploy.sh [options]

Upload an APK to the destination in STORIX_ANDROID_APK_DEST.

Options:
  -f, --file <path>     Upload the specified APK file
  -d, --dest <target>   Override STORIX_ANDROID_APK_DEST for this run
  -n, --dry-run         Print the resolved upload command without executing it
  -h, --help            Show this help
EOF
}

APK_PATH=""
DEST="${STORIX_ANDROID_APK_DEST:-}"
DRY_RUN=0

while [[ $# -gt 0 ]]; do
  case "$1" in
    -f|--file)
      APK_PATH="${2:-}"
      if [[ -z "$APK_PATH" ]]; then
        echo "[storix] --file requires a value"
        exit 1
      fi
      shift 2
      ;;
    -d|--dest)
      DEST="${2:-}"
      if [[ -z "$DEST" ]]; then
        echo "[storix] --dest requires a value"
        exit 1
      fi
      shift 2
      ;;
    -n|--dry-run)
      DRY_RUN=1
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "[storix] Unknown argument: $1"
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$DEST" ]]; then
  echo "[storix] STORIX_ANDROID_APK_DEST is not set and no --dest was provided"
  exit 1
fi

if [[ -z "$APK_PATH" ]]; then
  mapfile -t APK_CANDIDATES < <(find app/build/outputs/apk -type f -name '*.apk' -printf '%T@ %p\n' 2>/dev/null | sort -nr | awk '{print $2}')
  if [[ ${#APK_CANDIDATES[@]} -eq 0 ]]; then
    echo "[storix] No APK found under app/build/outputs/apk"
    exit 1
  fi
  APK_PATH="${APK_CANDIDATES[0]}"
fi

if [[ ! -f "$APK_PATH" ]]; then
  echo "[storix] APK not found: $APK_PATH"
  exit 1
fi

echo "[storix] APK: $APK_PATH"
echo "[storix] Destination: $DEST"

if [[ "$DRY_RUN" -eq 1 ]]; then
  echo "[storix] Dry run: scp \"$APK_PATH\" \"$DEST\""
  exit 0
fi

scp "$APK_PATH" "$DEST"
echo "[storix] Upload finished"
