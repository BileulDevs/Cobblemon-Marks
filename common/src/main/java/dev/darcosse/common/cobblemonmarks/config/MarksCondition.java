package dev.darcosse.common.cobblemonmarks.config;

/**
 * Mapping class that links a specific Mark identifier to its acquisition logic.
 * This is the object used by the configuration loader to register how
 * each Mark should be handled by the mod.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public class MarksCondition {

    /** The ResourceLocation or string ID of the Mark (e.g., "cobblemon:lunchtime_mark"). */
    private final String markIdentifier;

    /** The set of rules (triggers, requirements, exclusions) for this specific Mark. */
    private final Conditions conditions;

    /**
     * Constructs a mapping between a Mark and its logic.
     * * @param markIdentifier The unique ID of the Mark within the Cobblemon registry.
     * @param conditions     The logic tree required to obtain or progress toward this Mark.
     */
    public MarksCondition(String markIdentifier, Conditions conditions) {
        this.markIdentifier = markIdentifier;
        this.conditions = conditions;
    }

    /**
     * @return The identifier of the Mark.
     */
    public String getMarkIdentifier()    { return markIdentifier; }

    /**
     * @return The logic container associated with this Mark.
     */
    public Conditions getConditions()    { return conditions; }
}