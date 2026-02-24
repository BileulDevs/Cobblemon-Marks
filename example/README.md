# Cobblemon Custom Marks — Datapack Guide

This datapack allows you to add **custom marks** to Cobblemon — special ribbons or titles that can be obtained by Pokémon. This guide explains every file, every field, and how to create your own marks from scratch.

---

## 📁 Folder Structure

```
datapack-example/
├── pack.mcmeta
├── assets/
│   └── cobblemon/
│       ├── lang/
│       │   ├── en_us.json
│       │   └── fr_fr.json
│       └── textures/
│           └── gui/
│               mark/
│                   └── mark_example.png
└── data/
    └── cobblemon/
        └── marks/
            └── mark_example.json
```

---

## 📄 `pack.mcmeta`

This file is **required by Minecraft** to recognize the folder as a valid datapack/resource pack.

```json
{
  "pack": {
    "pack_format": 34,
    "description": "My custom Cobblemon marks"
  }
}
```

| Field | Description                                                            |
|---|------------------------------------------------------------------------|
| `pack_format` | The Minecraft pack format version. Use `34` for 1.21.x.                |
| `description` | A short text describing your pack. Shown in the pack selection screen. |

---

## 🗂️ `data/cobblemon/marks/mark_example.json`

This is the **core definition** of your custom mark. Each `.json` file in this folder defines one mark.

```json
{
  "name": "cobblemon:mark_example",
  "title": "cobblemon.mark.mark_example.title",
  "titleColor": "E040FB",
  "description": "cobblemon.mark.mark_example.desc",
  "texture": "cobblemon:textures/gui/mark/mark_example.png",
  "chance": 0.0,
  "indexNumber": 1000
}
```

| Field | Type | Description |
|---|---|---|
| `name` | `string` | The **unique identifier** of the mark, in the format `namespace:mark_id`. Must be unique across all datapacks. |
| `title` | `string` | Translation key pointing to the mark's **title** — the honorific displayed on the Pokémon (supports `%1$s`). By convention: `cobblemon.mark.<mark_id>.title`. |
| `titleColor` | `string` | Hex color code **(without `#`)** used to render the title text in-game. Example: `"E040FB"` for purple. |
| `description` | `string` | Translation key pointing to the mark's **description** shown in the summary screen. By convention: `cobblemon.mark.<mark_id>.desc`. |
| `texture` | `string` | Resource location of the mark's icon PNG, in the format `namespace:path/to/texture.png`. |
| `chance` | `float` | Probability between `0.0` and `1.0` for the mark to be **naturally assigned** to a wild Pokémon on spawn. `0.0` means it will never be assigned automatically. |
| `indexNumber` | `int` | Controls the **sort order** of marks in the UI. Lower numbers appear first. Use high values (e.g. `1000`) to push custom marks toward the end of the list. |

> ⚠️ The filename of this JSON (e.g. `mark_example.json`) does **not** need to match the `name` field, but it is strongly recommended to keep them consistent.

---

## 🌐 `assets/cobblemon/lang/en_us.json` & `fr_fr.json`

These files contain the **translated strings** for your mark's name and description. You can add as many language files as you want (e.g. `de_de.json`, `es_es.json`, etc.).

```json
{
  "cobblemon.mark.mark_example.title": "%1$s the Example",
  "cobblemon.mark.mark_example.desc": "This Pokémon has been marked as an example."
}
```

| Key | Description |
|---|---|
| `cobblemon.mark.<mark_id>.title` | The **honorific title** of the mark displayed in-game. Supports `%1$s` to insert the Pokémon's name. |
| `cobblemon.mark.<mark_id>.desc` | The **description** of the mark shown in the Pokémon's summary screen or tooltip. |

### 🔧 The `%1$s` Placeholder

The `%1$s` placeholder in the name string is automatically **replaced at runtime by the Pokémon's name**.

**Example:**
```json
{
  "cobblemon.mark.mark_example.name": "%1$s the Wanderer"
}
```
If a Pikachu has this mark, it will be displayed as **"Pikachu the Wanderer"**.

This allows you to create personalized mark titles that dynamically adapt to the Pokémon wearing them — just like the ribbon titles in the mainline Pokémon games.

> 💡 You can position `%1$s` anywhere in the string: `"%1$s the Brave"`, `"The Legendary %1$s"`, or even `"Champion %1$s of the Valley"`.

---

## 🖼️ `assets/cobblemon/textures/gui/mark/mark_example.png`

This is the **icon** displayed for your mark in the UI.

| Property | Recommended value |
|---|---|
| Format | PNG with transparency (RGBA) |
| Size | `16×16` pixels (standard) or `32×32` for higher resolution |
| Location | `assets/<namespace>/textures/gui/mark/<mark_id>.png` |

Make sure the path in your texture file exactly matches what you declared in the `texture` field of your mark JSON.

---

## 🚀 Creating Your Own Mark — Step by Step

1. **Copy** the `datapack-example` folder and rename it to your pack's name.
2. **Edit `pack.mcmeta`** to update the description.
3. **Rename `mark_example.json`** to `my_mark.json` inside `data/cobblemon/marks/`.
4. **Edit the JSON** — update the `name`, `title`, `description`, and `texture` fields.
5. **Add your translation keys** to both `en_us.json` and any other language files. Use `%1$s` in the name if you want the Pokémon's name to appear in the title.
6. **Replace `mark_example.png`** with your own icon (rename it to match your mark's ID).
7. **Drop the folder** into the `datapacks/` directory of your Minecraft world.
8. Run `/reload` in-game or restart the server.

---

## ✅ Checklist

- [ ] `pack.mcmeta` is present at the root
- [ ] Mark JSON is in `data/cobblemon/marks/`
- [ ] `name` field uses a unique `namespace:id`
- [ ] `title` and `description` keys match entries in all lang files
- [ ] `%1$s` is used in the name string if a Pokémon-name placeholder is desired
- [ ] Texture PNG is placed at the correct path and matches the `texture` field
- [ ] Both `en_us.json` and additional languages are populated

---

## 📚 Notes

- Namespace your marks with a unique prefix (your pack name) to avoid conflicts with other datapacks. In my case I used the Cobblemon namespace.
- Marks are not automatically assigned to Pokémon — you will need a separate mechanism (loot tables, commands, or another mod) to grant them.
- All translation keys across different mods and packs share the same lang files, so key names must be unique globally.


---

## ➕ Registering Your Mark's Condition In-Game

For your mark to be **unlockable in-game**, you need to add its condition entry to the mod's `conditions.json` file. This file is an **array** — each element defines the unlock condition for one mark.

The file is located at:
```
config/cobblemonmarks/conditions.json
```

Open it and add your entry inside the array, alongside the existing ones:

```json
[
  {
    "markIdentifier": "cobblemon:mark_example",
    "conditions": {
      "condition": {
        "type": "KILL",
        "requiredKills": 5,
        "requiredTypes": [],
        "requiredSpecies": ["zorua"],
        "nbtKey": "markfarm_example_kills"
      },
      "required": [],
      "excluded": []
    }
  }
]
```

> ⚠️ Make sure the file remains a **valid JSON** after editing — don't forget commas between entries, and never add a trailing comma after the last one.

Then enjoy your farming !