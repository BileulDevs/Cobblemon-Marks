package dev.darcosse.common.cobblemonmarks.config;

/**
 * Enumeration defining all supported types of Mark conditions.
 * This is used as a central registry to identify the nature of a condition
 * during logic processing and configuration parsing.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public enum ConditionType {
    /** Standard wild Pokémon defeat requirement. */
    KILL,

    /** Requirement based on defeating Pokémon while fishing. */
    FISHING_KILL,

    /** Requirement based on defeating a specific form of a Pokémon. */
    FORM_KILL,

    /** Requirement based on a consecutive win streak. */
    STREAK,

    /** Requirement based on successfully catching a Pokémon. */
    CATCH,

    /** Passive requirement: specific weather must be active. */
    WEATHER,

    /** Passive requirement: must be within a specific biome. */
    BIOME,

    /** Passive requirement: specific world time. */
    TIME,

    /** Requirement based on the time the battle actually occurred. */
    TIME_OF_BATTLE,

    /** Requirement based on the Pokémon's current level. */
    LEVEL,

    /** Passive requirement: must be in a specific dimension (Overworld, Nether, etc.). */
    DIMENSION,

    /** Requirement based on the Pokémon's current status effect. */
    STATUS,

    /** Requirement based on the Pokémon's physical size. */
    SIZE,

    /** Requirement based on the friendship/happiness level of the Pokémon. */
    FRIENDSHIP,

    /** Requirement based on the number of times the Pokémon has fainted. */
    DEATH,

    /** Requirement checking if the target or player Pokémon is shiny. */
    SHINY
}