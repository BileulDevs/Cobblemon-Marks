package dev.darcosse.common.cobblemonmarks.config;

import dev.darcosse.common.cobblemonmarks.config.condition.MarkCondition;
import java.util.List;

/**
 * Container class that aggregates multiple conditions to define a Mark's acquisition logic.
 * It separates the primary trigger from environmental or state requirements.
 *
 * @author Darcosse
 * @version 1.1
 * @since 2026
 */
public class Conditions {

    /** The primary trigger condition (typically a KillCondition, CatchCondition, or StreakCondition). */
    private final MarkCondition killCondition;

    /** List of additional conditions that MUST be met (e.g., Weather, Time, Biome). */
    private final List<MarkCondition> required;

    /** List of conditions that MUST NOT be met for the Mark to be awarded. */
    private final List<MarkCondition> excluded;

    /**
     * Full constructor for defining complex Mark logic.
     * * @param killCondition The main action trigger.
     * @param required      Conditions that must be true.
     * @param excluded      Conditions that must be false.
     */
    public Conditions(MarkCondition killCondition, List<MarkCondition> required,
                      List<MarkCondition> excluded) {
        this.killCondition = killCondition;
        this.required = required;
        this.excluded = excluded;
    }

    /**
     * Simplified constructor for Marks with triggers and requirements but no exclusions.
     */
    public Conditions(MarkCondition killCondition, List<MarkCondition> required) {
        this(killCondition, required, List.of());
    }

    /**
     * Basic constructor for Marks based solely on a single trigger action.
     */
    public Conditions(MarkCondition killCondition) {
        this(killCondition, List.of(), List.of());
    }

    /**
     * Constructor for Marks that don't have a specific "Kill/Catch" counter but
     * rely on state validation.
     */
    public Conditions(List<MarkCondition> required, List<MarkCondition> excluded) {
        this(null, required, excluded);
    }

    /**
     * Minimalist constructor for state-only requirements.
     */
    public Conditions(List<MarkCondition> required) {
        this(null, required, List.of());
    }

    /** @return The primary trigger condition. */
    public MarkCondition getKillCondition()        { return killCondition; }

    /** @return The list of mandatory conditions. */
    public List<MarkCondition> getRequired()       { return required; }

    /** @return The list of forbidden conditions. */
    public List<MarkCondition> getExcluded()       { return excluded; }
}