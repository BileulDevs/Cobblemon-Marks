package dev.darcosse.common.cobblemonmarks.config;

import dev.darcosse.common.cobblemonmarks.config.condition.*;

import java.util.ArrayList;
import java.util.List;

public class MarksConfig {

    public static List<MarksCondition> CONDITIONS = new ArrayList<>();
    public static final List<MarksCondition> DEFAULT_CONDITIONS = buildDefaults();

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
    private static final String FISHING          = COBBLEMON + "mark_fishing";
    private static final String UNCOMMON         = COBBLEMON + "mark_uncommon";
    private static final String RARE             = COBBLEMON + "mark_rare";
    private static final String PARTNER          = COBBLEMON + "mark_partner";
    private static final String RIBBON_TRAINING  = COBBLEMON + "ribbon_training";
    private static final String RIBBON_EARTH     = COBBLEMON + "ribbon_event_earth";

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

    private static List<MarksCondition> buildDefaults() {
        List<MarksCondition> list = new ArrayList<>();

        // =====================================================================
        // MARKS DE TEMPS
        // =====================================================================

        list.add(new MarksCondition(LUNCHTIME,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "lunchtime_kills"),
                        List.of(new TimeCondition(6000, 11833))
                )
        ));

        list.add(new MarksCondition(DUSK,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "dusk_kills"),
                        List.of(new TimeCondition(11834, 13701))
                )
        ));

        list.add(new MarksCondition(SLEEPY_TIME,
                new Conditions(
                        new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "sleepy_kills"),
                        List.of(new TimeCondition(13702, 22299))
                )
        ));

        list.add(new MarksCondition(DAWN,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "dawn_kills"),
                        List.of(new TimeCondition(22300, 5999))
                )
        ));

        // =====================================================================
        // MARKS DE MÉTÉO
        // =====================================================================

        list.add(new MarksCondition(CLOUDY,
                new Conditions(
                        new KillCondition(250, List.of(), List.of(), NBT_PREFIX + "cloudy_kills"),
                        List.of(new WeatherCondition(List.of(WeatherCondition.Weather.CLEAR))),
                        List.of(
                                new WeatherCondition(List.of(WeatherCondition.Weather.RAIN)),
                                new WeatherCondition(List.of(WeatherCondition.Weather.THUNDER)),
                                new WeatherCondition(List.of(WeatherCondition.Weather.SNOW))
                        )
                )
        ));

        list.add(new MarksCondition(MISTY,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "misty_kills"),
                        List.of(new BiomeCondition(List.of("#minecraft:increased_fire_burnout")))
                )
        ));

        list.add(new MarksCondition(RAINY,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "rainy_kills"),
                        List.of(new WeatherCondition(List.of(WeatherCondition.Weather.RAIN))),
                        List.of(
                                new BiomeCondition(List.of("#cobblemon:is_freezing")),
                                new BiomeCondition(List.of("#cobblemon:is_sandy"))
                        )
                )
        ));

        list.add(new MarksCondition(STORMY,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "stormy_kills"),
                        List.of(new WeatherCondition(List.of(WeatherCondition.Weather.THUNDER))),
                        List.of(
                                new BiomeCondition(List.of("#cobblemon:is_freezing")),
                                new BiomeCondition(List.of("#cobblemon:is_sandy"))
                        )
                )
        ));

        list.add(new MarksCondition(SNOWY,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "snowy_kills"),
                        List.of(new WeatherCondition(List.of(WeatherCondition.Weather.SNOW)))
                )
        ));

        list.add(new MarksCondition(BLIZZARD,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "blizzard_kills"),
                        List.of(
                                new WeatherCondition(List.of(WeatherCondition.Weather.THUNDER)),
                                new BiomeCondition(List.of("#cobblemon:is_freezing"))
                        )
                )
        ));

        list.add(new MarksCondition(DRY,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "dry_kills"),
                        List.of(
                                new WeatherCondition(List.of(WeatherCondition.Weather.RAIN)),
                                new TimeCondition(6000, 12000),
                                new BiomeCondition(List.of("#cobblemon:is_arid"))
                        )
                )
        ));

        list.add(new MarksCondition(SANDSTORM,
                new Conditions(
                        new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "sandstorm_kills"),
                        List.of(
                                new WeatherCondition(List.of(WeatherCondition.Weather.RAIN, WeatherCondition.Weather.THUNDER)),
                                new BiomeCondition(List.of("#cobblemon:is_sandy"))
                        )
                )
        ));

        // =====================================================================
        // MARKS SPÉCIALES
        // =====================================================================

        list.add(new MarksCondition(FISHING,
                new Conditions(
                        new FishingKillCondition(150, List.of(), List.of(), NBT_PREFIX + "fishing_kills")
                )
        ));

        list.add(new MarksCondition(UNCOMMON,
                new Conditions(
                        new KillCondition(250, List.of(), List.of(), NBT_PREFIX + "uncommon_kills")
                )
        ));

        list.add(new MarksCondition(RARE,
                new Conditions(
                        new CatchCondition(3, NBT_PREFIX + "rare_shiny_captures"),
                        List.of(new ShinyCondition())
                )
        ));

        list.add(new MarksCondition(PARTNER,
                new Conditions(
                        new KillCondition(500, List.of(), List.of(), NBT_PREFIX + "partner_kills")
                )
        ));

        list.add(new MarksCondition(RIBBON_TRAINING,
                new Conditions(
                        new KillCondition(1000, List.of(), List.of(), NBT_PREFIX + "ribbon_training_kills")
                )
        ));

        // =====================================================================
        // MARKS DE PERSONNALITÉ
        // =====================================================================

        list.add(new MarksCondition(ABSENT_MINDED,
                new Conditions(
                        new KillCondition(150, List.of("psychic"), List.of(), NBT_PREFIX + "absentminded_kills")
                )
        ));

        list.add(new MarksCondition(ANGRY,
                new Conditions(
                        new KillCondition(100, List.of("fighting", "dark"), List.of(), NBT_PREFIX + "angry_kills")
                )
        ));

        list.add(new MarksCondition(CALMNESS,
                new Conditions(
                        new KillCondition(125, List.of("fairy", "normal"), List.of(), NBT_PREFIX + "calmness_kills")
                )
        ));

        list.add(new MarksCondition(EXCITED,
                new Conditions(
                        new FormKillCondition(100, List.of("alolan", "galarian", "hisuian", "paldean"), NBT_PREFIX + "excited_kills")
                )
        ));

        list.add(new MarksCondition(CRAFTY,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "crafty_kills"),
                        List.of(new StatusCondition(List.of("psn", "brn", "tox")))
                )
        ));

        list.add(new MarksCondition(CHARISMATIC,
                new Conditions(
                        new CatchCondition(5, NBT_PREFIX + "charismatic_form_kills"),
                        List.of(new ShinyCondition())
                )
        ));

        list.add(new MarksCondition(FEROCIOUS,
                new Conditions(
                        new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "ferocious_kills"),
                        List.of(new LevelCondition(50, 100))
                )
        ));

        list.add(new MarksCondition(FLUSTERED,
                new Conditions(List.of(
                        new DeathCondition(20, NBT_PREFIX + "flustered_deaths")
                ))
        ));

        list.add(new MarksCondition(HUMBLE,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "humble_kills"),
                        List.of(new LevelCondition(true))
                )
        ));

        list.add(new MarksCondition(INTELLECTUAL,
                new Conditions(
                        new KillCondition(150, List.of("psychic"), List.of("unown"), NBT_PREFIX + "intellectual_kills"),
                        List.of(new DimensionCondition(List.of("minecraft:the_end")))
                )
        ));

        list.add(new MarksCondition(INTENSE,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "intense_kills"),
                        List.of(new TimeOfBattleCondition(null, 1))
                )
        ));

        list.add(new MarksCondition(JITTERY,
                new Conditions(List.of(
                        new DeathCondition(30, NBT_PREFIX + "jittery_deaths")
                ))
        ));

        list.add(new MarksCondition(JOYFUL,
                new Conditions(
                        new KillCondition(200, List.of(), List.of(
                                "pikachu", "jigglypuff", "clefairy", "togepi", "skitty",
                                "sylveon", "togekiss", "happiny", "chansey", "blissey",
                                "comfey", "shaymin", "victini", "plusle", "minun",
                                "pachirisu", "emolga", "dedenne", "alcremie", "bellossom",
                                "cherrim", "lilligant", "eevee", "audino", "teddiursa",
                                "spinda", "ribombee", "cutiefly", "wigglytuff", "vulpix"
                        ), NBT_PREFIX + "joyful_kills")
                )
        ));

        list.add(new MarksCondition(KINDLY,
                new Conditions(List.of(
                        new FriendshipCondition(250)
                ))
        ));

        list.add(new MarksCondition(PEEVED,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "peeved_kills"),
                        List.of(new StatusCondition(List.of("slp", "par")))
                )
        ));

        list.add(new MarksCondition(PRIDEFUL,
                new Conditions(
                        new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "prideful_kills"),
                        List.of(new LevelCondition(100, 100))
                )
        ));

        list.add(new MarksCondition(PUMPED_UP,
                new Conditions(
                        new StreakCondition(50, NBT_PREFIX + "pumpedup_streak")
                )
        ));

        list.add(new MarksCondition(RIBBON_EARTH,
                new Conditions(
                        new StreakCondition(100, NBT_PREFIX + "ribbon_earth_streak")
                )
        ));

        list.add(new MarksCondition(ROWDY,
                new Conditions(
                        new KillCondition(125, List.of(), List.of(), NBT_PREFIX + "rowdy_kills"),
                        List.of(new SizeCondition(List.of(SizeCondition.Size.XL, SizeCondition.Size.XXL, SizeCondition.Size.XXXL))),
                        List.of()
                )
        ));

        list.add(new MarksCondition(SCOWLING,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "scowling_kills"),
                        List.of(new StatusCondition(List.of("slp", "par")))
                )
        ));

        list.add(new MarksCondition(SLUMP,
                new Conditions(List.of(
                        new DeathCondition(40, NBT_PREFIX + "slump_deaths")
                ))
        ));

        list.add(new MarksCondition(SMILEY,
                new Conditions(
                        new KillCondition(200, List.of("normal"), List.of(), NBT_PREFIX + "smiley_kills")
                )
        ));

        list.add(new MarksCondition(TEARY,
                new Conditions(
                        new KillCondition(150, List.of(), List.of(), NBT_PREFIX + "teary_kills"),
                        List.of(new SizeCondition(List.of(SizeCondition.Size.XS, SizeCondition.Size.XXS, SizeCondition.Size.XXXS))),
                        List.of()
                )
        ));

        list.add(new MarksCondition(THORNY,
                new Conditions(
                        new KillCondition(100, List.of("grass", "poison"), List.of(), NBT_PREFIX + "thorny_kills")
                )
        ));

        list.add(new MarksCondition(UNSURE,
                new Conditions(
                        new KillCondition(100, List.of(), List.of(), NBT_PREFIX + "unsure_kills"),
                        List.of(new StatusCondition(List.of("par")))
                )
        ));

        list.add(new MarksCondition(UPBEAT,
                new Conditions(
                        new KillCondition(200, List.of(), List.of(
                                "jigglypuff", "wigglytuff", "politoed", "exploud", "loudred",
                                "whismur", "chimecho", "kricketune", "chatot", "meloetta",
                                "primarina", "toxtricity", "skeledirge", "altaria", "gardevoir",
                                "maractus", "ludicolo", "noivern", "rillaboom", "bronzong",
                                "chingling", "misdreavus", "kommoo"
                        ), NBT_PREFIX + "upbeat_kills")
                )
        ));

        list.add(new MarksCondition(VIGOR,
                new Conditions(
                        new KillCondition(200, List.of("fighting"), List.of(), NBT_PREFIX + "vigor_kills")
                )
        ));

        list.add(new MarksCondition(ZERO_ENERGY,
                new Conditions(List.of(
                        new DeathCondition(75, NBT_PREFIX + "zeroenergy_deaths")
                ))
        ));

        list.add(new MarksCondition(ZONED_OUT,
                new Conditions(
                        new KillCondition(100, List.of("psychic", "fairy"), List.of(), NBT_PREFIX + "zonedout_kills"),
                        List.of(new TimeCondition(13702, 22299))
                )
        ));

        return list;
    }
}