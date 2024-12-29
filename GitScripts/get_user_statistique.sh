#!/bin/bash
# A propos: Script pour obtenir les statistiques des utilisateurs Git
# Date: 30 octobre 2024
# Auteur: Vazgen Markaryan (get_user_statistique.sh)
# Auteur: François Lacoursière (gitcount.sh)
# Support intéllectuel: Chat GPT + GitHub Copilot
# Version: 1.0

# Fonctionnement:
# Ce script génère des statistiques sur les contributions des utilisateurs dans un dépôt Git.
# Il peut afficher les résultats dans la console ou les exporter en format JSON.
# Les statistiques incluent les lignes de code ajoutées et supprimées pour chaque utilisateur et pour chaque sprint, ainsi que l'impact total sur le projet.

# Utilisation:
# ./get_user_statistique.sh {console|json}
# - console: Affiche les résultats dans la console.
# - json: Exporte les résultats en format JSON.

# Remarques:
# - Les noms d'utilisateur peuvent être censurés avec "showRealNames=false" pour des raisons de confidentialité.
# - Les sprints sont définis pour la session d'automne 2024.
# - Le script utilise un autre script "gitcount.sh" pour extraire les statistiques Git.

# Vérifie si le premier argument est "console" ou "json"
if [ "$1" != "console" ] && [ "$1" != "json" ]; then
        echo "Usage: $0 {console|json}"
        exit 1
fi

# Censure les noms d'utilisateur pour des raisons de confidentialité
declare -A name_mapping=(
        ["Vazgen"]="User1"
        ["Joan"]="User2"
        ["Alex"]="User3"
        ["Bernard"]="User4"
        ["Lou"]="User5"
)

# Utilise les noms d'utilisateur tels qu'ils sont dans le dépôt Git
users=("Vazgen" "Joan" "Alex" "Bernard" "Lou")

# Variable pour déterminer si les noms d'utilisateur doivent être affichés
showRealNames=true

# Modifiez si vous souhaitez l'utiliser pour des sessions autres que l'automne 2024
sprints=(
        "2024-09-11 2024-09-25"
        "2024-09-26 2024-10-09"
        "2024-10-10 2024-10-23"
        "2024-10-24 2024-11-06"
        "2024-11-07 2024-11-20"
        "2024-11-21 2024-12-04"
)

declare -A total_impact_per_user
declare -A total_java_per_user
declare -A total_js_per_user
declare -A total_json_per_user

# Function to calculate the total lines of code in the project
calculate_total_project_lines() {
        git ls-files '*.java' '*.js' 'front-end/fructose/src/utilities/i18n/locals/*.json' | xargs wc -l | tail -n 1 | awk '{print $1}'
}

# Fonction pour extraire les lignes ajoutées et supprimées pour un langage spécifique
extract_lines() {
        local result="$1"
        local language="$2"
        local added=$(echo "$result" | grep "$language:" | awk -F'Added: ' '{print $2}' | awk -F', Deleted' '{print $1}' | tr -d ' ')
        local deleted=$(echo "$result" | grep "$language:" | awk -F'Deleted: ' '{print $2}' | awk -F', Impact' '{print $1}' | tr -d ' ')
        echo "$added $deleted"
}

# Fonction pour calculer l'impact d'un utilisateur sur tous les sprints
calculate_user_impact() {
        local user="$1"
        local total_java_added=0
        local total_java_deleted=0
        local total_js_added=0
        local total_js_deleted=0
        local total_json_added=0
        local total_json_deleted=0

        for sprint in "${sprints[@]}"; do
                local sprint_start_date=$(echo "$sprint" | awk '{print $1}')
                local sprint_end_date=$(echo "$sprint" | awk '{print $2}')
                local result=$(./GitScripts/gitcount.sh "$user" "$sprint_start_date" "$sprint_end_date")

                read java_added java_deleted <<<$(extract_lines "$result" "Java")
                read js_added js_deleted <<<$(extract_lines "$result" "JavaScript")
                read json_added json_deleted <<<$(extract_lines "$result" "JSON")

                total_java_added=$((total_java_added + java_added))
                total_java_deleted=$((total_java_deleted + java_deleted))
                total_js_added=$((total_js_added + js_added))
                total_js_deleted=$((total_js_deleted + js_deleted))
                total_json_added=$((total_json_added + json_added))
                total_json_deleted=$((total_json_deleted + json_deleted))
        done

        total_impact_per_user["$user"]=$(((total_java_added - total_java_deleted) + (total_js_added - total_js_deleted) + (total_json_added - total_json_deleted)))
        total_java_per_user["$user"]=$((total_java_added - total_java_deleted))
        total_js_per_user["$user"]=$((total_js_added - total_js_deleted))
        total_json_per_user["$user"]=$((total_json_added - total_json_deleted))
}

# Fonction pour afficher le total de l'impact pour chaque utilisateur
display_total_written_lines_per_user() {
        sorted_users=($(sort_users_by_impact))
        for user in "${sorted_users[@]}"; do
                calculate_user_impact "$user"
                display_name=$user
                if [ "$showRealNames" = false ]; then
                        display_name=${name_mapping[$user]}
                fi

                echo "$display_name: TOTAL PROJECT IMPACT" "$((total_java_per_user[$user] + total_js_per_user[$user] + total_json_per_user[$user]))"
                printf "Java       : %-6d\n" "$((total_java_per_user[$user]))"
                printf "JavaScript : %-6d\n" "$((total_js_per_user[$user]))"
                printf "JSON       : %-6d\n" "$((total_json_per_user[$user]))"
                echo "|--------------------------------------------------|"
                echo ""
        done
}

# Fonction pour calculer et afficher le pourcentage d'impact pour chaque utilisateur
display_impact_percentages_per_user() {
        echo ""
        declare -A impact_percentage_per_user
        total_percentage=0
        total_lines_counted=0
        total_project_impact=$(calculate_total_project_lines)

        for user in "${users[@]}"; do
                user_impact=${total_impact_per_user["$user"]}
                impact_percentage=$(awk "BEGIN {printf \"%.2f\", ($user_impact / $total_project_impact) * 100}")
                impact_percentage_per_user["$user"]=$impact_percentage
                total_percentage=$(awk "BEGIN {print $total_percentage + $impact_percentage}")
                total_lines_counted=$((total_lines_counted + user_impact))
        done

        sorted_users=($(for user in "${!impact_percentage_per_user[@]}"; do
                echo "$user:${impact_percentage_per_user[$user]}"
        done | sort -t: -k2 -nr | awk -F: '{print $1}'))

        for user in "${sorted_users[@]}"; do
                java_lines=${total_java_per_user["$user"]}
                js_lines=${total_js_per_user["$user"]}
                json_lines=${total_json_per_user["$user"]}
                total_lines=$((java_lines + js_lines + json_lines))
                display_name=$user
                if [ "$showRealNames" = false ]; then
                        display_name=${name_mapping[$user]}
                fi
                printf "%-10s: %6.2f%% (%4d lignes / %4d lignes)\n" "$display_name" "${impact_percentage_per_user[$user]}" "$total_lines" "$total_project_impact"
        done

        percentage_difference=$(awk "BEGIN {print 100 - $total_percentage}")
        lines_difference=$((total_project_impact - total_lines_counted))

        echo ""

        if (($(awk "BEGIN {print ($percentage_difference > 0)}"))); then
                echo "Avertissement: Marge d'erreur est de $percentage_difference% ($lines_difference lignes ne sont pas trouvé par rapport à ligne totaux du projet)"
        fi
}

# Fonction pour afficher l'impact par sprint pour chaque utilisateur
display_sprint_impact_per_user_per_sprint() {
        sorted_users=($(sort_users_by_impact))
        for user in "${sorted_users[@]}"; do
                display_name=$user
                if [ "$showRealNames" = false ]; then
                        display_name=${name_mapping[$user]}
                fi

                for i in "${!sprints[@]}"; do
                        sprint=${sprints[$i]}
                        sprint_start_date=$(echo "$sprint" | awk '{print $1}')
                        sprint_end_date=$(echo "$sprint" | awk '{print $2}')
                        result=$(./GitScripts/gitcount.sh "$user" "$sprint_start_date" "$sprint_end_date")

                        read java_added java_deleted <<<$(extract_lines "$result" "Java")
                        read js_added js_deleted <<<$(extract_lines "$result" "JavaScript")
                        read json_added json_deleted <<<$(extract_lines "$result" "JSON")

                        sprint_impact=$(((java_added - java_deleted) + (js_added - js_deleted) + (json_added - json_deleted)))

                        printf "%-10s Sprint %d (%s %s)\n" "$display_name" "$((i + 1))" "$sprint_start_date" "$sprint_end_date"
                        printf "Java       : +%-6d, -%-6d, Impact: %-6d\n" "$java_added" "$java_deleted" "$((java_added - java_deleted))"
                        printf "JavaScript : +%-6d, -%-6d, Impact: %-6d\n" "$js_added" "$js_deleted" "$((js_added - js_deleted))"
                        printf "JSON       : +%-6d, -%-6d, Impact: %-6d\n" "$json_added" "$json_deleted" "$((json_added - json_deleted))"
                        printf "TOTAL      : +%-6d, -%-6d, Impact: %-6d\n" "$(($java_added + $js_added + $json_added))" "$(($java_deleted + $js_deleted + $json_deleted))" "$((($java_added + $js_added + $json_added) - ($java_deleted + $js_deleted + $json_deleted)))"
                        echo ""
                        echo "|--------------------------------------------------|"
                        echo ""
                done
        done
}

sort_users_by_impact() {
        sorted_users=($(for user in "${!total_impact_per_user[@]}"; do
                echo "$user:${total_impact_per_user[$user]}"
        done | sort -t: -k2 -nr | awk -F: '{print $1}'))
        echo "${sorted_users[@]}"
}

# Fonction qui généré fichier JSON
json_output() {
        echo "Generating JSON output..."

        total_project_impact=$(calculate_total_project_lines)

        json_output="{"
        json_output+="\"total_project_impact\": \"$total_project_impact\","
        json_output+="\"users\": ["

        # Calculer l'impact de chaque utilisateur
        for user in "${users[@]}"; do
                calculate_user_impact "$user"
        done

        # Sort Utilisateurs par impact
        sorted_users=($(sort_users_by_impact))

        # Boucle à travers les utilisateurs
        for user in "${sorted_users[@]}"; do
                display_name=$user
                if [ "$showRealNames" = false ]; then
                        display_name=${name_mapping[$user]}
                fi

                json_output+="{"
                json_output+="\"name\": \"$display_name\","
                json_output+="\"sprints\": [{"

                total_java_added=0
                total_java_deleted=0
                total_js_added=0
                total_js_deleted=0
                total_json_added=0
                total_json_deleted=0

                # Boucle à travers les sprints
                for i in "${!sprints[@]}"; do
                        sprint=${sprints[$i]}
                        sprint_start_date=$(echo $sprint | awk '{print $1}')
                        sprint_end_date=$(echo $sprint | awk '{print $2}')
                        result=$(./GitScripts/gitcount.sh "$user" "$sprint_start_date" "$sprint_end_date")

                        read java_added java_deleted <<<$(extract_lines "$result" "Java")
                        read js_added js_deleted <<<$(extract_lines "$result" "JavaScript")
                        read json_added json_deleted <<<$(extract_lines "$result" "JSON")

                        total_java_added=$((total_java_added + java_added))
                        total_java_deleted=$((total_java_deleted + java_deleted))
                        total_js_added=$((total_js_added + js_added))
                        total_js_deleted=$((total_js_deleted + js_deleted))
                        total_json_added=$((total_json_added + json_added))
                        total_json_deleted=$((total_json_deleted + json_deleted))

                        total_added=$((java_added + js_added + json_added))
                        total_deleted=$((java_deleted + js_deleted + json_deleted))
                        total_impact=$((total_added - total_deleted))

                        json_output+="\"sprint_$((i + 1))\": {"
                        json_output+="\"start_date\": \"$sprint_start_date\","
                        json_output+="\"end_date\": \"$sprint_end_date\","
                        json_output+="\"java_added\": $java_added,"
                        json_output+="\"java_deleted\": $java_deleted,"
                        json_output+="\"js_added\": $js_added,"
                        json_output+="\"js_deleted\": $js_deleted,"
                        json_output+="\"json_added\": $json_added,"
                        json_output+="\"json_deleted\": $json_deleted,"
                        json_output+="\"total_added\": $total_added,"
                        json_output+="\"total_deleted\": $total_deleted,"
                        json_output+="\"total_impact\": $total_impact"
                        json_output+="},"
                done

                # Enlever la dernière virgule
                json_output=${json_output%,}
                json_output+="},"

                # Section totale pour l'utilisateur
                total_user_impact=$((total_java_added - total_java_deleted + total_js_added - total_js_deleted + total_json_added - total_json_deleted))
                json_output+="{"
                json_output+="\"total\": {"
                json_output+="\"java_lines\": $((total_java_added - total_java_deleted)),"
                json_output+="\"js_lines\": $((total_js_added - total_js_deleted)),"
                json_output+="\"json_lines\": $((total_json_added - total_json_deleted)),"
                json_output+="\"total_impact\": $total_user_impact"
                json_output+="}}],"

                # Pourcentage d'impact pour l'utilisateur
                impact_percentage=$(awk "BEGIN {printf \"%.2f\", ($total_user_impact / $total_project_impact) * 100}")
                json_output+="\"percentage\": $impact_percentage"
                json_output+="},"
        done

        # Enlever la dernière virgule
        json_output=${json_output%,}
        json_output+="]}"

        # Debugging: Print JSON généré sur la console pour déboguer
        # echo $json_output

        # Écrire la sortie JSON dans un fichier, formaté avec jq
        echo "$json_output" | jq --indent 7 . >front-end/fructose/src/components/project_impact.json
        echo "JSON output generated successfully. Check 'GitScripts/project_impact.json'."
}

# Fonction qui print résultat dans Console
console_output() {
        echo "Patientez, le calcul est en cours..."
        echo ""
        # Calculer l'impact de chaque utilisateur
        for user in "${users[@]}"; do
                calculate_user_impact "$user"
        done

        # Afficher l'impact par sprint pour chaque utilisateur
        display_sprint_impact_per_user_per_sprint "$user"

        # Afficher le total des lignes écrites par chaque utilisateur
        display_total_written_lines_per_user "$user"

        # Afficher les lignes totales du projet
        echo "TOTAL LIGNES DE CODE DANS LE PROJET: $(calculate_total_project_lines)"

        # Afficher le pourcentage d'impact pour chaque utilisateur
        display_impact_percentages_per_user
}

# Executer la fonction appropriée en fonction de l'argument passé
if [ "$1" = "console" ]; then
        console_output
elif [ "$1" = "json" ]; then
        json_output
fi