---
title: Profile Command
tags:
  - comando
  - profile
---

## Comando /profile

### Parámetros
- **nick** — Nombre del invocador
- **tag** — Tag de Riot (ej: LAS, 1234)
- **region** — Región del servidor

### Flujo
1. Usuario ejecuta `/profile nick tag region`
2. `Profile.execute()` extrae los parámetros
3. Crea instancia de `ApiRiot` con nick, tag, región
4. `LolProfileService.profilesBuilder()` consulta SoloQ y Flex
5. Genera embeds con rank, LP, wins, losses, winrate
6. Envía los embeds al canal de Discord

### Embeds
- Color según tier → [[EmbedColor]]
- Imagen según tier → [[Images]] (URLs de Imgur)
- Campos: Queue Type, Tier + Rank, LP, Wins, Losses, Winrate

### Estado Actual
- Funcional pero `Main.java` y `CommandRegisterService` están comentados
- Falta mejorar manejo de errores
