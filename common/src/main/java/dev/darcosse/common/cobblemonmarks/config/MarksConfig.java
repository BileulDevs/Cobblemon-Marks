package dev.darcosse.common.cobblemonmarks.config;

import dev.darcosse.common.cobblemonmarks.config.condition.*;

import java.util.List;

public class MarksConfig {
    private static final String NBT_PREFIX = "markfarm_";
    private static final String COBBLEMON  = "cobblemon:";

    // === Marks de temps ===
    private static final String LUNCHTIME    = COBBLEMON + "mark_time_lunchtime";
    private static final String DUSK         = COBBLEMON + "mark_time_dusk";
    private static final String SLEEPY_TIME  = COBBLEMON + "mark_time_sleepy-time";
    private static final String DAWN         = COBBLEMON + "mark_time_dawn";

    // === Marks de météo ===
    private static final String CLOUDY       = COBBLEMON + "mark_weather_cloudy";
    private static final String MISTY        = COBBLEMON + "mark_weather_misty";
    private static final String RAINY        = COBBLEMON + "mark_weather_rainy";
    private static final String STORMY       = COBBLEMON + "mark_weather_stormy";
    private static final String SNOWY        = COBBLEMON + "mark_weather_snowy";
    private static final String BLIZZARD     = COBBLEMON + "mark_weather_blizzard";
    private static final String DRY          = COBBLEMON + "mark_weather_dry";
    private static final String SANDSTORM    = COBBLEMON + "mark_weather_sandstorm";

    // === Marks spéciales ===
    private static final String FISHING              = COBBLEMON + "mark_fishing";
    private static final String UNCOMMON             = COBBLEMON + "mark_uncommon";
    private static final String RARE                 = COBBLEMON + "mark_rare";
    private static final String PARTNER              = COBBLEMON + "mark_partner";
    private static final String RIBBON_TRAINING      = COBBLEMON + "ribbon_training";

    // === Marks de personnalité ===
    private static final String ABSENT_MINDED = COBBLEMON + "mark_personality_absent-minded";
    private static final String ANGRY         = COBBLEMON + "mark_personality_angry";
    private static final String CALMNESS      = COBBLEMON + "mark_personality_calmness";
    private static final String CHARISMATIC   = COBBLEMON + "mark_personality_charismatic";
    private static final String CRAFTY        = COBBLEMON + "mark_personality_crafty";
    private static final String EXCITED       = COBBLEMON + "mark_personality_excited";
    private static final String FEROCIOUS     = COBBLEMON + "mark_personality_ferocious";
    private static final String FLUSTERED     = COBBLEMON + "mark_personality_flustered";
    private static final String HUMBLE        = COBBLEMON + "mark_personality_humble";
    private static final String INTELLECTUAL  = COBBLEMON + "mark_personality_intellectual";
    private static final String INTENSE       = COBBLEMON + "mark_personality_intense";
    private static final String JITTERY       = COBBLEMON + "mark_personality_jittery";
    private static final String JOYFUL        = COBBLEMON + "mark_personality_joyful";
    private static final String KINDLY        = COBBLEMON + "mark_personality_kindly";
    private static final String PEEVED        = COBBLEMON + "mark_personality_peeved";
    private static final String PRIDEFUL      = COBBLEMON + "mark_personality_prideful";
    private static final String PUMPED_UP     = COBBLEMON + "mark_personality_pumped-up";
    private static final String ROWDY         = COBBLEMON + "mark_personality_rowdy";
    private static final String SCOWLING      = COBBLEMON + "mark_personality_scowling";
    private static final String SLUMP         = COBBLEMON + "mark_personality_slump";
    private static final String SMILEY        = COBBLEMON + "mark_personality_smiley";
    private static final String TEARY         = COBBLEMON + "mark_personality_teary";
    private static final String THORNY        = COBBLEMON + "mark_personality_thorny";
    private static final String UNSURE        = COBBLEMON + "mark_personality_unsure";
    private static final String UPBEAT        = COBBLEMON + "mark_personality_upbeat";
    private static final String VIGOR         = COBBLEMON + "mark_personality_vigor";
    private static final String ZERO_ENERGY   = COBBLEMON + "mark_personality_zero-energy";
    private static final String ZONED_OUT     = COBBLEMON + "mark_personality_zoned-out";

    public static List<MarksCondition> CONDITIONS = List.of(

            // =====================================================================
            // MARKS DE TEMPS
            // Fenêtre horaire contraignante → 100 kills
            // Sleepy-Time : longue nuit → 150
            // =====================================================================

            // Lunchtime Mark (the Peckish) — 100 kills entre 6000-11833
            new MarksCondition(LUNCHTIME,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "lunchtime_kills"),
                            List.of(new TimeCondition(6000, 11833))
                    )
            ),

            // Dusk Mark (the Dozy) — 100 kills entre 11834-13701
            new MarksCondition(DUSK,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "dusk_kills"),
                            List.of(new TimeCondition(11834, 13701))
                    )
            ),

            // Sleepy-Time Mark (the Sleepy) — 150 kills entre 13702-22299 (longue fenêtre)
            new MarksCondition(SLEEPY_TIME,
                    new Conditions(
                            new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "sleepy_kills"),
                            List.of(new TimeCondition(13702, 22299))
                    )
            ),

            // Dawn Mark (the Early Riser) — 100 kills entre 22300-5999 (traverse minuit)
            new MarksCondition(DAWN,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "dawn_kills"),
                            List.of(new TimeCondition(22300, 5999))
                    )
            ),

            // =====================================================================
            // MARKS DE MÉTÉO
            // Météo aléatoire → 100 kills (double/triple contrainte → 100 aussi)
            // Temps clair très fréquent → 250
            // =====================================================================

            // Cloudy Mark (the Cloud Watcher) — 250 kills par temps clair (très commun)
            new MarksCondition(CLOUDY,
                    new Conditions(
                            new KillCondition(250, List.of(), List.of(), NBT_PREFIX + "cloudy_kills"),
                            List.of(new WeatherCondition(List.of(WeatherCondition.Weather.CLEAR))),
                            List.of(
                                    new WeatherCondition(List.of(WeatherCondition.Weather.RAIN)),
                                    new WeatherCondition(List.of(WeatherCondition.Weather.THUNDER)),
                                    new WeatherCondition(List.of(WeatherCondition.Weather.SNOW))
                            )
                    )
            ),

            // Misty Mark (the Mist Drifter) — 100 kills dans un biome brumeux
            new MarksCondition(MISTY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "misty_kills"),
                            List.of(new BiomeCondition(List.of("#minecraft:increased_fire_burnout")))
                    )
            ),

            // Rainy Mark (the Sodden) — 100 kills sous la pluie hors biomes gelés/sableux
            new MarksCondition(RAINY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "rainy_kills"),
                            List.of(new WeatherCondition(List.of(WeatherCondition.Weather.RAIN))),
                            List.of(
                                    new BiomeCondition(List.of("#cobblemon:is_freezing")),
                                    new BiomeCondition(List.of("#cobblemon:is_sandy"))
                            )
                    )
            ),

            // Stormy Mark (the Thunderstruck) — 100 kills sous l'orage hors biomes gelés/sableux
            new MarksCondition(STORMY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "stormy_kills"),
                            List.of(new WeatherCondition(List.of(WeatherCondition.Weather.THUNDER))),
                            List.of(
                                    new BiomeCondition(List.of("#cobblemon:is_freezing")),
                                    new BiomeCondition(List.of("#cobblemon:is_sandy"))
                            )
                    )
            ),

            // Snowy Mark (the Snow Frolicker) — 100 kills sous la neige
            new MarksCondition(SNOWY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "snowy_kills"),
                            List.of(new WeatherCondition(List.of(WeatherCondition.Weather.SNOW)))
                    )
            ),

            // Blizzard Mark (the Shivering) — 100 kills sous tempête de neige en biome gelé
            new MarksCondition(BLIZZARD,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "blizzard_kills"),
                            List.of(
                                    new WeatherCondition(List.of(WeatherCondition.Weather.THUNDER)),
                                    new BiomeCondition(List.of("#cobblemon:is_freezing"))
                            )
                    )
            ),

            // Dry Mark (the Parched) — 100 kills sous pluie dans biome aride le jour (triple contrainte)
            new MarksCondition(DRY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "dry_kills"),
                            List.of(
                                    new WeatherCondition(List.of(WeatherCondition.Weather.RAIN)),
                                    new TimeCondition(6000, 12000),
                                    new BiomeCondition(List.of("#cobblemon:is_arid"))
                            )
                    )
            ),

            // Sandstorm Mark (the Sandswept) — 150 kills sous pluie/orage dans biome sableux
            new MarksCondition(SANDSTORM,
                    new Conditions(
                            new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "sandstorm_kills"),
                            List.of(
                                    new WeatherCondition(List.of(WeatherCondition.Weather.RAIN, WeatherCondition.Weather.THUNDER)),
                                    new BiomeCondition(List.of("#cobblemon:is_sandy"))
                            )
                    )
            ),

            // =====================================================================
            // MARKS SPÉCIALES
            // =====================================================================

            // Fishing Mark (the Catch of the Day) — 150 kills de pokémon pêchés
            new MarksCondition(FISHING,
                    new Conditions(
                            new FishingKillCondition(150, List.of(), List.of(), NBT_PREFIX + "fishing_kills")
                    )
            ),

            // Uncommon Mark (the Sociable) — 250 kills sans condition (la plus accessible)
            new MarksCondition(UNCOMMON,
                    new Conditions(
                            new KillCondition(250, List.of(), List.of(), NBT_PREFIX + "uncommon_kills")
                    )
            ),

            // Rare Mark (the Recluse) — Capturer 1 pokémon shiny en combat (très rare, 1 suffit)
            new MarksCondition(RARE,
                    new Conditions(
                            new KillCondition(1, List.of(), List.of(), NBT_PREFIX + "rare_shiny_captures"),
                            List.of(new ShinyCondition())
                    )
            ),

            // Partner Mark (the Reliable Partner) — 500 kills
            new MarksCondition(PARTNER,
                    new Conditions(
                            new KillCondition(500, List.of(), List.of(), NBT_PREFIX + "partner_kills")
                    )
            ),

            new MarksCondition(RIBBON_TRAINING,
                    new Conditions(
                            new KillCondition(1000, List.of(), List.of(), NBT_PREFIX + "ribbon_training_kills")
                    )
            ),


            // =====================================================================
            // MARKS DE PERSONNALITÉ
            // Sans contrainte → 200-250 kills
            // Avec contrainte de type → 150-200 kills
            // Avec double contrainte → 100-150 kills
            // =====================================================================

            // Absent-Minded Mark (the Spacey) — 150 kills de type Psy
            new MarksCondition(ABSENT_MINDED,
                    new Conditions(
                            new KillCondition(150, List.of("psychic"), List.of(), NBT_PREFIX + "absentminded_kills")
                    )
            ),

            // Angry Mark (the Furious) — 200 kills de type Combat ou Ténèbres
            new MarksCondition(ANGRY,
                    new Conditions(
                            new KillCondition(200, List.of("fighting", "dark"), List.of(), NBT_PREFIX + "angry_kills")
                    )
            ),

            // Calmness Mark (the Serene) — 200 kills de type Fée ou Normal
            new MarksCondition(CALMNESS,
                    new Conditions(
                            new KillCondition(200, List.of("fairy", "normal"), List.of(), NBT_PREFIX + "calmness_kills")
                    )
            ),

            // Charismatic Mark (the Radiant) — Capturer 3 pokémon shiny en combat
            new MarksCondition(CHARISMATIC,
                    new Conditions(
                            new KillCondition(3, List.of(), List.of(), NBT_PREFIX + "charismatic_shiny_captures"),
                            List.of(new ShinyCondition())
                    )
            ),

            // Crafty Mark (the Opportunist) — 100 kills de pokémon empoisonnés ou brûlés
            new MarksCondition(CRAFTY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "crafty_kills"),
                            List.of(new StatusCondition(List.of("psn", "brn", "tox")))
                    )
            ),

            // Excited Mark (the Giddy) — 50 victoires consécutives sans tomber
            new MarksCondition(EXCITED,
                    new Conditions(
                            new StreakCondition(50, NBT_PREFIX + "excited_streak")
                    )
            ),

            // Ferocious Mark (the Rampaging) — 150 kills de pokémon niveau 50+
            new MarksCondition(FEROCIOUS,
                    new Conditions(
                            new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "ferocious_kills"),
                            List.of(new LevelCondition(50, null))
                    )
            ),

            // Flustered Mark (the Easily Flustered) — Notre pokémon meurt 20 fois
            new MarksCondition(FLUSTERED,
                    new Conditions(List.of(
                            new DeathCondition(20, NBT_PREFIX + "flustered_deaths")
                    ))
            ),

            // Humble Mark (the Humble) — 100 kills de pokémon plus forts que nous
            new MarksCondition(HUMBLE,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "humble_kills"),
                            List.of(new LevelCondition(true))
                    )
            ),

            // Intellectual Mark (the Scholar) — 150 kills de type Psy dans le End
            new MarksCondition(INTELLECTUAL,
                    new Conditions(
                            new KillCondition(150, List.of("psychic"), List.of("unown"), NBT_PREFIX + "intellectual_kills"),
                            List.of(new DimensionCondition(List.of("minecraft:the_end")))
                    )
            ),

            // Intense Mark (the Feisty) — 100 kills en 1 seul tour
            new MarksCondition(INTENSE,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "intense_kills"),
                            List.of(new TimeOfBattleCondition(null, 1))
                    )
            ),

            // Jittery Mark (the Anxious) — Notre pokémon meurt 30 fois
            new MarksCondition(JITTERY,
                    new Conditions(List.of(
                            new DeathCondition(30, NBT_PREFIX + "jittery_deaths")
                    ))
            ),

            // Joyful Mark (the Joyful) — 200 kills de pokémon joyeux
            new MarksCondition(JOYFUL,
                    new Conditions(
                            new KillCondition(200, List.of(), List.of("pikachu", "jigglypuff", "clefairy", "togepi", "skitty", "sylveon", "togekiss"), NBT_PREFIX + "joyful_kills")
                    )
            ),

            // Kindly Mark (the Kindhearted) — Friendship 200+ (instantané)
            new MarksCondition(KINDLY,
                    new Conditions(List.of(
                            new FriendshipCondition(250)
                    ))
            ),

            // Peeved Mark (the Grumpy) — 100 kills de pokémon paralysés ou endormis
            new MarksCondition(PEEVED,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "peeved_kills"),
                            List.of(new StatusCondition(List.of("slp", "par")))
                    )
            ),

            // Prideful Mark (the Arrogant) — 150 kills de pokémon niveau 100
            new MarksCondition(PRIDEFUL,
                    new Conditions(
                            new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "prideful_kills"),
                            List.of(new LevelCondition(100, 100))
                    )
            ),

            // Pumped-Up Mark (the Driven) — 50 victoires consécutives sans tomber
            new MarksCondition(PUMPED_UP,
                    new Conditions(
                            new StreakCondition(50, NBT_PREFIX + "pumpedup_streak")
                    )
            ),

            // Rowdy Mark (the Rowdy) — 100 kills de pokémon de grande taille
            new MarksCondition(ROWDY,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "rowdy_kills"),
                            List.of(new SizeCondition(List.of(SizeCondition.Size.XL, SizeCondition.Size.XXL, SizeCondition.Size.XXXL)))
                    )
            ),

            // Scowling Mark (the Stern) — 100 kills avec notre pokémon paralysé ou endormi
            new MarksCondition(SCOWLING,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "scowling_kills"),
                            List.of(new StatusCondition(List.of("slp", "par")))
                    )
            ),

            // Slump Mark (the Worn-Out) — Notre pokémon meurt 30 fois
            new MarksCondition(SLUMP,
                    new Conditions(List.of(
                            new DeathCondition(30, NBT_PREFIX + "slump_deaths")
                    ))
            ),

            // Smiley Mark (the Beaming) — 200 kills de type Normal
            new MarksCondition(SMILEY,
                    new Conditions(
                            new KillCondition(200, List.of("normal"), List.of(), NBT_PREFIX + "smiley_kills")
                    )
            ),

            // Teary Mark (the Teary-Eyed) — Notre pokémon meurt 50 fois
            new MarksCondition(TEARY,
                    new Conditions(List.of(
                            new DeathCondition(50, NBT_PREFIX + "teary_deaths")
                    ))
            ),

            // Thorny Mark (the Pompous) — 200 kills de type Plante ou Poison
            new MarksCondition(THORNY,
                    new Conditions(
                            new KillCondition(200, List.of("grass", "poison"), List.of(), NBT_PREFIX + "thorny_kills")
                    )
            ),

            // Unsure Mark (the Reluctant) — 100 kills en étant paralysé
            new MarksCondition(UNSURE,
                    new Conditions(
                            new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "unsure_kills"),
                            List.of(new StatusCondition(List.of("par")))
                    )
            ),

            // Upbeat Mark (the Chipper) — 200 kills de pokémon joyeux
            new MarksCondition(UPBEAT,
                    new Conditions(
                            new KillCondition(200, List.of(), List.of("pikachu", "jigglypuff", "skitty", "togepi", "marill", "snubbull", "togekiss"), NBT_PREFIX + "upbeat_kills")
                    )
            ),

            // Vigor Mark (the Lively) — 200 kills de type Combat
            new MarksCondition(VIGOR,
                    new Conditions(
                            new KillCondition(200, List.of("fighting"), List.of(), NBT_PREFIX + "vigor_kills")
                    )
            ),

            // Zero Energy Mark (the Apathetic) — Notre pokémon meurt 100 fois
            new MarksCondition(ZERO_ENERGY,
                    new Conditions(List.of(
                            new DeathCondition(100, NBT_PREFIX + "zeroenergy_deaths")
                    ))
            ),

            // Zoned-Out Mark (the Daydreamer) — 150 kills de type Psy la nuit
            new MarksCondition(ZONED_OUT,
                    new Conditions(
                            new KillCondition(150, List.of("psychic"), List.of(), NBT_PREFIX + "zonedout_kills"),
                            List.of(new TimeCondition(13702, 22299))
                    )
            )
    );
}