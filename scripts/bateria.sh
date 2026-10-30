#!/bin/bash
# Ejecuta la batería de preguntas contra el microservicio y muestra la respuesta y las fuentes.
# Uso: ./scripts/bateria.sh [archivo_de_preguntas]
set -u

URL="${CHATBOT_URL:-http://localhost:8082}/api/chat"
ARCHIVO="${1:-scripts/preguntas.txt}"
numero=0

while IFS= read -r pregunta || [ -n "$pregunta" ]; do
  case "$pregunta" in
    ''|\#*) continue ;;
  esac
  numero=$((numero + 1))
  inicio=$(date +%s)
  cuerpo=$(jq -n --arg p "$pregunta" '{pregunta: $p}')
  respuesta=$(curl -s -X POST "$URL" -H "Content-Type: application/json" -d "$cuerpo")
  fin=$(date +%s)

  echo "=== $numero. $pregunta ($((fin - inicio)) s)"
  echo "$respuesta" | jq -r '"Respuesta: \(.respuesta // .mensaje)\nFuentes: \((.fuentes // []) | join(", "))"'
  echo
done < "$ARCHIVO"