#!/bin/bash

# $1 = author
# $2 = date since (yyyy-mm-dd)
# $3 = date until (yyyy-mm-dd)
if [ -z "$1" ]; then
    echo "No author supplied"
    echo "Usage: ./gitcount.sh author 2023-09-01 2023-09-30"
    exit 1
fi
if [ -z "$2" ]; then
    echo "No date since supplied. Ex: 2023-09-21"
    echo "Usage: ./gitcount.sh author 2023-09-01 2023-09-30"
    exit 1
fi
if [ -z "$3" ]; then
    echo "No date until supplied. Ex: 2023-09-30"
    exit 1
fi

author="$1"
date_since="$2"
date_until="$3"
declare -A ext_names=( ["java"]="Java" ["js"]="JavaScript" ["json"]="JSON" )

for ext in "${!ext_names[@]}"; do
    if [ "$ext" == "json" ]; then
        pattern="front-end\/fructose\/src\/utilities\/i18n\/locals\/.*\.$ext$"
    else
        pattern=".*src\/.*\.$ext$"
    fi
    result=$(git log --branches --no-merges --numstat --pretty="%H %as" --author="$author" --since="$date_since" --until="$date_until" \
    | grep -E "$pattern" | grep -v node_modules \
    | awk '{plus+=$1; minus+=$2; total=plus-minus} END {printf("Added: %d, Deleted: %d, Impact %d", plus, minus, total)}')
    echo "${ext_names[$ext]}: $result"
done