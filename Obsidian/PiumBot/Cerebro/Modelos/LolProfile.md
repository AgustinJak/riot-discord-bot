---
title: LolProfile
tags:
  - modelo
  - lol
---

## LolProfile

Extiende [[RiotProfile]]. Representa el perfil ranked de un jugador de LoL.

### Campos
- `rank` — División (I, II, III, IV)
- `tier` — Tier (IRON, BRONZE, SILVER, GOLD, PLATINUM, EMERALD, DIAMOND, MASTER, GRANDMASTER, CHALLENGER)
- `queueType` — Tipo de cola (RANKED_SOLO_5x5, RANKED_FLEX_SR)
- `leaguePoints` — LP actuales
- `wins` — Victorias
- `losses` — Derrotas

### Heredados de RiotProfile
- `riotUser` — Nombre de Riot
- `puuid` — ID único
- `region` — Región API
- `server` — Servidor

### Métodos
- `embedBuilder()` — Genera un `MessageEmbed` de Discord con toda la info
- `showUserStats()` — Salida en texto plano

### Stubs Pendientes
- **TftProfile** — Sin implementar
- **ValProfile** — Sin implementar
