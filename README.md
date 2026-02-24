# ⚔ CobblemonMarks

> A Cobblemon addon that lets you farm Marks by completing configurable objectives.

---

## 📖 Overview

CobblemonMarks adds a **mark farming system** to Cobblemon. Each mark can be configured with conditions that the player must meet to unlock it. Locked marks are displayed in the Pokémon's mark tab as **grayed-out icons** with a tooltip showing the objectives and current progress — updated in real time.

---

## ✨ Features

- 🏷️ **Lock/unlock system** — locked marks appear grayed out in the summary screen
- 📊 **Real-time progress** — objectives sync from server to client instantly
- 🎯 **Fully configurable** — define conditions for any mark via JSON
- 🔒 **Duplicate protection** — if the same mark appears twice in the config, only the first entry is kept
- 🌍 **Multilingual** — supported languages: `en_us`, `fr_fr`, `de_de`, `es_es`, `pt_br`, `ja_jp`, `zh_cn`
- ⚙️ **Multi-loader** — supports both **Fabric** and **NeoForge** (via Architectury)

---

## 🧩 Condition Types

### Main condition (`condition`)

The `condition` field defines the primary objective — it tracks progress and determines when the mark is awarded.

| Type | Description |
|------|-------------|
| `KILL` | Defeat a number of Pokémon (with optional type/species filters) |
| `FISHING_KILL` | Defeat Pokémon caught by fishing |
| `FORM_KILL` | Defeat Pokémon of a specific form (e.g. `alolan`, `mega`) |
| `STREAK` | Win battles in a row without fainting |
| `CATCH` | Catch Pokémon in battle |
| `DEATH` | Faint a number of times |

### Additional conditions (`required` / `excluded`)

These conditions filter when progress can be made. `required` conditions must all be met, `excluded` conditions must not be met.

| Type | Description |
|------|-------------|
| `WEATHER` | Specific weather (`CLEAR`, `RAIN`, `THUNDER`, `SNOW`) |
| `TIME` | In-game time range (in ticks, 0–24000) |
| `TIME_OF_BATTLE` | Win within a number of turns |
| `BIOME` | Specific biome or biome tag |
| `LEVEL` | Opponent level range |
| `SIZE` | Pokémon size (`XXXS` → `XXXL`) |
| `DIMENSION` | Specific dimension |
| `STATUS` | Status condition on your Pokémon |
| `FRIENDSHIP` | Minimum friendship level |
| `SHINY` | Target must be shiny (used with `CATCH`) |

---

## 🗂️ Configuration

A default `conditions.json` is generated on first launch at:
```
config/cobblemonmarks/conditions.json
```

You can edit it freely. On next launch, the mod will load your custom configuration. If a `markIdentifier` appears more than once, only the first entry is kept.

You can see every Marks available [here](https://gitlab.com/cable-mc/cobblemon/-/tree/main/common/src/main/resources/data/cobblemon/marks?ref_type=heads)

You can also create your own marks by following this [tuto](https://github.com/BileulDevs/Cobblemon-Marks/tree/main/example)

### Example

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
        {
          "type": "TIME",
          "minTime": 6000,
          "maxTime": 11833
        }
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
        {
          "type": "SHINY"
        }
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

---

## 🖥️ In-game UI

When hovering over a **locked mark** in the Pokémon summary screen:
- 🔒 The mark name and title are displayed
- 📊 Current progress is shown (e.g. `Progress: 12/100`)
- 📋 All conditions are listed with their respective icons and colors

When hovering over an **already obtained mark**:
- The mark title is displayed in italic with its own color
- Conditions and progress are still shown for reference

---

## 🚀 Installation

1. Install [Cobblemon](https://modrinth.com/mod/cobblemon)
2. Download CobblemonMarks for your platform (Fabric or NeoForge)
3. Drop the jar into your `mods/` folder
4. Launch the game — a default config will be generated on first run

### Requirements

| Dependency | Version |
|------------|---------|
| Minecraft | 1.21.1 |
| Cobblemon | ≥ 1.7.1, < 1.8.0 |
| Fabric API *(Fabric only)* | Latest for 1.21.1 |

---

## 🌐 Supported Languages

| Code | Language |
|------|----------|
| `en_us` | English |
| `fr_fr` | Français |
| `de_de` | Deutsch |
| `es_es` | Español |
| `pt_br` | Português (Brasil) |
| `ja_jp` | 日本語 |
| `zh_cn` | 中文（简体）|

---

## 🛠️ For Developers

The mod is built with **Architectury** and targets both Fabric and NeoForge from a single common codebase.

```
common/   → shared logic (conditions, handler, mixin, config)
fabric/   → Fabric entrypoints and network handler
neoforge/ → NeoForge entrypoints and network handler
```

Progress synchronization uses a custom `S2C` packet (`SyncMarkProgressPayload`) that sends the Pokémon's full progress map to the client whenever a counter is incremented, ensuring the tooltip always reflects the latest state without needing to reopen the summary screen.

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

## 👤 Author

Made by **Darcosse**