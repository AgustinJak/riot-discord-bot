---
title: Estructura del Proyecto
tags:
  - arquitectura
  - estructura
---

## Estructura de Paquetes

```
com.pium
├── Main.java                         # Entry point
└── riot/
    ├── api/                          # Capa de comunicación con Riot API
    │   ├── apiconfig/
    │   │   └── ApiRiot.java          # Cliente principal de la API
    │   ├── config/
    │   │   ├── Sources.java          # Enum URLs (sin usar aún)
    │   │   └── UrlBuilder.java       # Construcción de URLs
    │   └── model/
    │       ├── RiotProfile.java      # Clase abstracta base
    │       ├── LolProfile.java       # Perfil de LoL
    │       ├── TftProfile.java       # Stub vacío
    │       └── ValProfile.java       # Stub vacío
    ├── botservice/                   # Inicialización del bot
    │   ├── BotLauncherService.java   # Lanza JDA
    │   ├── CommandRegisterService.java # Registra slash commands
    │   └── config/
    │       ├── RegionConfig.java     # Opciones de región para Discord
    │       └── utils/
    │           └── Region.java       # Constantes y mapeo de regiones
    └── commandservice/               # Lógica de comandos
        ├── commands/
        │   ├── profilecommand/
        │   │   ├── Profile.java      # Handler del comando /profile
        │   │   └── LolProfileService.java # Lógica de fetch
        │   └── utils/
        │       ├── EmbedColor.java   # Mapeo rank → color
        │       ├── EmbedConfigBuilder.java # Builder de embeds
        │       └── Images.java       # Mapeo rank → imagen
        └── riotcommandmanagerservice/
            ├── RiotBotCommand.java    # Interfaz de comandos
            └── RiotCommandManagerService.java # Dispatcher
```

## Dependencias Principales

- **JDA 5.3.0** — Java Discord API
- **dotenv-java 3.0.0** — Variables de entorno
- **Lavaplayer 2.2.3** — Audio (sin usar aún)
- **org.json** — Parsing JSON
- **Lombok** — Boilerplate reduction

## Flujo de Datos

```
Discord → RiotCommandManagerService → Profile.execute()
    → ApiRiot (getPuuid → getLolProfile)
    → LolProfile.embedBuilder()
    → Discord Embed Response
```
