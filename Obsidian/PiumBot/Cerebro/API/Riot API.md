---
title: Riot API
tags:
  - api
  - riot
---

## Endpoints Usados

### Account por PUUID
```
https://{region}.api.riotgames.com/riot/account/v1/accounts/by-riot-id/{nick}/{tag}?api_key={key}
```
Regiones: `americas`, `europe`, `asia`, `sea`

### League Entries
```
https://{server}.api.riotgames.com/lol/league/v4/entries/by-summoner/{puuid}?api_key={key}
```
Servers: `la2`, `la1`, `br1`, `na1`, `euw1`, `kr`, `jp1`, `ph2`

## Mapeo Región → Server

| Región | API Region | Server |
|--------|-----------|--------|
| LAS    | americas  | la2    |
| LAN    | americas  | la1    |
| BR     | americas  | br1    |
| NA     | americas  | na1    |
| EUW    | europe    | euw1   |
| KR     | asia      | kr     |
| JP     | asia      | jp1    |
| SEA    | sea       | ph2    |

## Respuesta de League
Devuelve un **array JSON**. Índice 0 = SoloQ, Índice 1 = Flex (si existen).
Campos: `tier`, `rank`, `queueType`, `leaguePoints`, `wins`, `losses`

## Problemas Conocidos
- Cuentas de LAN a veces devuelven array vacío → manejado en el código
- Sin manejo de rate limits actualmente
