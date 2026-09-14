# Cobblemon Marks

> A Cobblemon addon that turns Marks into goals you farm, instead of luck you wait for.

---

## Overview

By default, Marks are handed out at random when a Pokémon spawns. This addon locks them behind objectives you define: defeat a hundred Pokémon at lunchtime, catch three shinies, win fifty battles in a row.

Locked marks show up greyed out in the Pokémon's mark tab, with a tooltip listing every condition and your live progress toward it. Nothing to open, nothing to refresh — the counter updates as you play.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green?logo=minecraft)](https://www.minecraft.net)
[![Fabric](https://img.shields.io/badge/Fabric-supported-dbb37d?logo=fabric)](https://fabricmc.net)
[![NeoForge](https://img.shields.io/badge/NeoForge-supported-e04e14)](https://neoforged.net)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## Features

- **Lock/unlock system** — locked marks appear greyed out in the summary screen
- **Live progress** — counters sync server to client the moment they change, no need to reopen anything
- **Fully configurable** — any mark, any combination of conditions, defined in JSON
- **Duplicate protection** — if a mark appears twice in the config, only the first entry is kept
- **Multi-loader** — Fabric and NeoForge from one codebase, via Architectury
- **Seven languages** — English, French, German, Spanish, Portuguese (BR), Japanese, Simplified Chinese

---

## Conditions

A mark has one **main condition**, which counts progress and decides when the mark is awarded, plus any number of **filters** that decide whether a given action counts at all.

### Main conditions

| Type | Counts |
|---|---|
| `KILL` | Pokémon defeated, optionally filtered by type or species |
| `FISHING_KILL` | Pokémon defeated after being caught by fishing |
| `FORM_KILL` | Pokémon of a specific form defeated (`alolan`, `mega`, …) |
| `CATCH` | Pokémon caught in battle |
| `STREAK` | Battles won in a row without fainting |
| `DEATH` | Times your Pokémon has fainted |

### Filters

`required` conditions must all hold. `excluded` conditions must not. Either way, progress only moves when the filters agree.

| Type | Checks |
|---|---|
| `WEATHER` | `CLEAR`, `RAIN`, `THUNDER`, `SNOW` |
| `TIME` | In-game time range, in ticks (0–24000) |
| `TIME_OF_BATTLE` | Battle won within a number of turns |
| `BIOME` | A biome or a biome tag |
| `DIMENSION` | A specific dimension |
| `LEVEL` | Opponent level range |
| `SIZE` | Pokémon size, `XXXS` through `XXXL` |
| `STATUS` | Status condition on your Pokémon |
| `FRIENDSHIP` | Minimum friendship |
| `SHINY` | Target is shiny — pairs with `CATCH` |

---

## Configuration

A default `conditions.json` is written on first launch to:

```
config/cobblemonmarks/conditions.json
```

Edit it freely; it's reloaded on the next launch. If the same `markIdentifier` appears more than once, only the first entry survives.

The full list of vanilla Cobblemon marks is [here](https://gitlab.com/cable-mc/cobblemon/-/tree/main/common/src/main/resources/data/cobblemon/marks?ref_type=heads), and you can add marks of your own by following [this guide](https://github.com/BileulDevs/Cobblemon-Marks/tree/main/example).

### Example

Three marks: a hundred kills but only around midday, three shiny catches, and a fifty-battle streak.

```json
[
  {
    "markIdentifier": "cobblemon:mark_time_lunchtime",
    "conditions": {
      "condition": {
        "type": "KILL",
        "requiredKills": 100,
        "requiredTypes": [],
        "requiredSpecies": [],
        "nbtKey": "markfarm_lunchtime_kills"
      },
      "required": [
        { "type": "TIME", "minTime": 6000, "maxTime": 11833 }
      ],
      "excluded": []
    }
  },
  {
    "markIdentifier": "cobblemon:mark_rare",
    "conditions": {
      "condition": {
        "type": "CATCH",
        "requiredCount": 3,
        "nbtKey": "markfarm_rare_shiny_captures"
      },
      "required": [
        { "type": "SHINY" }
      ],
      "excluded": []
    }
  },
  {
    "markIdentifier": "cobblemon:mark_personality_pumped-up",
    "conditions": {
      "condition": {
        "type": "STREAK",
        "requiredStreak": 50,
        "nbtKey": "markfarm_pumpedup_streak"
      },
      "required": [],
      "excluded": []
    }
  }
]
```

`nbtKey` is where the counter is stored on the Pokémon. Give every mark its own, or two marks will share progress.

---

## In game

Hover a **locked mark** in the summary screen and you get its name, your current progress (`12/100`), and every condition listed with its own icon and colour.

Hover one you **already own** and the title shows in italics with its mark colour — conditions and progress stay visible, so you can still see what earned it.

---

## Installation

1. Install [Cobblemon](https://modrinth.com/mod/cobblemon) 1.8.0 for Minecraft 1.21.1.
2. Install the Kotlin bridge for your loader — Fabric Language Kotlin on Fabric, Kotlin for Forge on NeoForge. Cobblemon needs it.
3. Drop the Cobblemon Marks jar for your loader into `mods/`.
4. Launch — the default config is generated on first run.

### Requirements

| | Fabric | NeoForge |
|---|---|---|
| Minecraft | 1.21.1 | 1.21.1 |
| Cobblemon | 1.8.0+ | 1.8.0+ |
| Also required | Fabric API, Fabric Language Kotlin | Kotlin for Forge |

### Client or server?

On a multiplayer server, install it on **both**. The server owns the counters and the config; the client needs it to draw the locked state and the tooltips. The config is synced from server to client, so players automatically see whatever objectives that server defines.

---

## For developers

Architectury multiloader layout, one shared codebase:

```
common/   → conditions, handler, mixins, config
fabric/   → Fabric entrypoints and network handler
neoforge/ → NeoForge entrypoints and network handler
```

Progress syncing uses a custom S2C packet, `SyncMarkProgressPayload`, which pushes the Pokémon's whole progress map whenever a counter moves. That's what keeps the tooltip current without reopening the screen. A second packet, `SyncMarksConfigPayload`, hands the server's condition set to the client on join.

Adding a condition type means an entry in `ConditionType`, a class in `config/condition/`, a branch in `MarkConditionAdapter`, and a line in `MarkConditionDescriber` for the tooltip.

---

## License

MIT — see [LICENSE](LICENSE). Use it in your modpacks freely.

Made by **Darcosse**. Bug reports and suggestions go to the [issue tracker](https://github.com/BileulDevs/Cobblemon-Marks/issues).