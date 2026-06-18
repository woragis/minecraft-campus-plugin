#!/bin/sh
set -e

PAPER_JAR=/opt/paper/paper.jar
PLUGIN_SRC=/opt/paper/plugins/CampusWorld.jar
DATA=/data

mkdir -p "$DATA/plugins/CampusWorld" "$DATA/plugins/Geyser-Spigot" "$DATA/plugins/floodgate"

cp -f "$PLUGIN_SRC" "$DATA/plugins/CampusWorld.jar"

# Geyser + Floodgate (baked into image at build time)
for jar in Geyser-Spigot Floodgate-Spigot; do
  if [ -f "/opt/paper/plugins/${jar}.jar" ]; then
    cp -f "/opt/paper/plugins/${jar}.jar" "$DATA/plugins/${jar}.jar"
  fi
done

export CAMPUS_API_URL="${CAMPUS_API_URL:-http://127.0.0.1:8080}"
export PLUGIN_API_KEY="${PLUGIN_API_KEY:-dev-plugin-key}"
export SERVER_SLUG="${SERVER_SLUG:-crossplay}"

envsubst '${CAMPUS_API_URL} ${PLUGIN_API_KEY} ${SERVER_SLUG}' \
  < /opt/paper/config.yml.template > "$DATA/plugins/CampusWorld/config.yml"

echo "eula=true" > "$DATA/eula.txt"

if [ ! -f "$DATA/server.properties" ]; then
  cat > "$DATA/server.properties" <<EOF
online-mode=true
motd=CampusWorld Cross-play
server-port=25565
level-name=world
EOF
fi

if [ ! -d "$DATA/world" ] && [ -n "${LEVEL_SEED:-}" ]; then
  if ! grep -q '^level-seed=' "$DATA/server.properties" 2>/dev/null; then
    echo "level-seed=${LEVEL_SEED}" >> "$DATA/server.properties"
  fi
fi

cd "$DATA"
exec java -Xms"${JAVA_MEMORY_MIN:-2G}" -Xmx"${JAVA_MEMORY_MAX:-4G}" \
  -jar "$PAPER_JAR" nogui
