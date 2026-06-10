# CampusWorld — Plugin

Plugin Paper do **CampusWorld** — whitelist, convites e sync com a API Go.

## Fase 1

- Whitelist no login (`AsyncPlayerPreLoginEvent` → API)
- `/invite <jogador>` → cria convite no backend
- `/campus status` → verifica se a API está online
- `/guild create|join|leave` → guildas via API
- Probation bloqueia `/invite` e `/guild` in-game
- Heartbeat no join → `POST /v1/internal/players/upsert`

## Requisitos

- **JDK 21** (ou deixar o Gradle baixar via toolchain)
- Gradle Wrapper (`./gradlew`)

Não é necessário ter Minecraft instalado para **compilar** e rodar **testes unitários**.

## Build

```bash
./gradlew build
```

Artefato: `build/libs/CampusWorld-0.1.0.jar` → copiar para `plugins/` do Paper.

## Configuração (`plugins/CampusWorld/config.yml`)

```yaml
api:
  base-url: "http://127.0.0.1:8080"
  plugin-key: "dev-plugin-key"   # mesmo PLUGIN_API_KEY do backend
  timeout-ms: 5000
server:
  slug: "vanilla"
```

## Testar in-game (quando tiver Paper)

1. Subir backend: `docker compose up -d` (pasta `backend/`)
2. Bootstrap do fundador: `scripts/bootstrap-first-player.sql`
3. Baixar [Paper 1.21](https://papermc.io/downloads/paper)
4. Copiar o `.jar` do plugin para `plugins/`
5. `java -jar paper.jar` (nogui)
6. Fundador entra → `/invite <username>` → convidado tenta entrar

## Estrutura

```text
src/main/java/com/woragis/campusworld/
├── CampusWorldPlugin.java
├── api/CampusWorldApiClient.java
├── commands/InviteCommand.java
├── listeners/WhitelistListener.java
└── listeners/PlayerJoinListener.java
```

## Documentação

- [Backend](../backend/README.md)
- [CampusWorld spec](../CAMPUSWORLD.md)
