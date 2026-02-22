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
- 🌍 **Multilingual** — supported languages: `en_us`, `fr_fr`, `de_de`, `es_es`, `pt_br`, `ja_jp`, `zh_cn`
- ⚙️ **Multi-loader** — supports both **Fabric** and **NeoForge** (via Architectury)

---

## 🧩 Condition Types

| Condition | Description |
|-----------|-------------|
| `KillCondition` | Defeat a number of Pokémon (with optional type/species filters) |
| `FishingKillCondition` | Defeat Pokémon caught by fishing |
| `FormKillCondition` | Defeat Pokémon of a specific form |
| `StreakCondition` | Win battles in a row without fainting |
| `CatchCondition` | Catch Pokémon in battle |
| `DeathCondition` | Faint a number of times |
| `WeatherCondition` | Require specific weather (Rain, Thunder, Snow, Clear) |
| `TimeCondition` | Require a specific in-game time range |
| `BiomeCondition` | Require a specific biome |
| `LevelCondition` | Require a specific opponent level range |
| `SizeCondition` | Require a specific Pokémon size (XXXS → XXXL) |
| `DimensionCondition` | Require a specific dimension |
| `StatusCondition` | Require a specific status condition |
| `FriendshipCondition` | Require a minimum friendship level |
| `ShinyCondition` | Require catching a shiny Pokémon in battle |
| `TimeOfBattleCondition` | Win within a certain number of turns |

Conditions can be set as **required** (✔) or **excluded** (✘).

---

## 🗂️ Configuration

Place your mark configurations in:
```
config/cobblemonmarks/marks.json
```

### Example

```json
[
  {
    "markIdentifier": "cobblemon:mark_time_lunchtime",
    "conditions": {
      "killCondition": {
        "type": "kill",
        "requiredCount": 100,
        "requiredTypes": ["normal"],
        "requiredSpecies": [],
        "nbtKey": "cobblemonmarks_lunchtime_kills"
      },
      "required": [
        {
          "type": "time",
          "minTime": 6000,
          "maxTime": 12000
        }
      ],
      "excluded": []
    }
  }
]
```

---

## 🖥️ In-game UI

When hovering over a locked mark in the Pokémon summary screen:

- 🔒 The mark name and title are displayed
- 📊 Current progress is shown (e.g. `Progress: 12/100`)
- 📋 All conditions are listed with their respective icons and colors

When hovering over an **already obtained** mark:
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

Progress synchronization uses a custom `S2C` packet (`SyncMarkProgressPayload`) that sends the Pokémon's full progress map to the client whenever a counter is incremented.

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

## 👤 Author

Made by **Darcosse**