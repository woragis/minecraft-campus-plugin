#!/bin/sh
set -e

PAPER_JAR=/opt/paper/paper.jar
PLUGIN_SRC=/opt/paper/plugins/CampusWorld.jar
DATA=/data

if [ ! -f "$PAPER_JAR" ]; then
  echo "FATAL: Paper jar missing at $PAPER_JAR (image build may have failed)."
  ls -la /opt/paper/ 2>/dev/null || true
  exit 1
fi
if [ ! -f "$PLUGIN_SRC" ]; then
  echo "FATAL: CampusWorld plugin jar missing at $PLUGIN_SRC"
  exit 1
fi

mkdir -p "$DATA/plugins/CampusWorld"
cp -f "$PLUGIN_SRC" "$DATA/plugins/CampusWorld.jar"

export CAMPUS_API_URL="${CAMPUS_API_URL:-http://127.0.0.1:8080}"
export PLUGIN_API_KEY="${PLUGIN_API_KEY:-dev-plugin-key}"
export SERVER_SLUG="${SERVER_SLUG:-vanilla}"

envsubst '${CAMPUS_API_URL} ${PLUGIN_API_KEY} ${SERVER_SLUG}' \
  < /opt/paper/config.yml.template > "$DATA/plugins/CampusWorld/config.yml"

echo "eula=true" > "$DATA/eula.txt"

# Seed only applies before the overworld folder exists.
if [ ! -d "$DATA/world" ] && [ -n "${LEVEL_SEED:-}" ]; then
  if [ ! -f "$DATA/server.properties" ]; then
    cat > "$DATA/server.properties" <<EOF
level-seed=${LEVEL_SEED}
level-name=world
online-mode=true
motd=CampusWorld
server-port=25565
EOF
  elif ! grep -q '^level-seed=' "$DATA/server.properties" 2>/dev/null; then
    echo "level-seed=${LEVEL_SEED}" >> "$DATA/server.properties"
  fi
fi

cd "$DATA"
exec java -Xms"${JAVA_MEMORY_MIN:-2G}" -Xmx"${JAVA_MEMORY_MAX:-4G}" \
  -jar "$PAPER_JAR" nogui
