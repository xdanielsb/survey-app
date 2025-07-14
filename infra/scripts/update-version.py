#!/bin/bash

# Parse arguments
for arg in "$@"; do
  case $arg in
    --previous-version=*)
      PREV_VERSION="${arg#*=}"
      shift
      ;;
    --next-version=*)
      NEXT_VERSION="${arg#*=}"
      shift
      ;;
    --path=*)
      TARGET_DIR="${arg#*=}"
      shift
      ;;
    *)
      echo "Unknown option: $arg"
      exit 1
      ;;
  esac
done

# Check required inputs
if [ -z "$PREV_VERSION" ] || [ -z "$NEXT_VERSION" ]; then
  echo "Usage: $0 --previous-version=patronx:2.1.0 --next-version=patronx:2.4.2 [--path=./some-directory]"
  exit 1
fi

# Default path is current directory
TARGET_DIR="${TARGET_DIR:-.}"

# Escape special characters for sed
ESCAPED_PREV=$(printf '%s\n' "$PREV_VERSION" | sed 's/[.[\*^$/]/\\&/g')
ESCAPED_NEXT=$(printf '%s\n' "$NEXT_VERSION" | sed 's/[&/\]/\\&/g')

# Replace in all files under target directory (in place, no backups)
echo "Replacing '$PREV_VERSION' with '$NEXT_VERSION' in files under '$TARGET_DIR'..."
find "$TARGET_DIR" -type f -exec sed -i "s/$ESCAPED_PREV/$ESCAPED_NEXT/g" {} +

echo "Done."

