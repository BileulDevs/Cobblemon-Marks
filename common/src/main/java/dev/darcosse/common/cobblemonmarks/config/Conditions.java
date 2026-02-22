package dev.darcosse.common.cobblemonmarks.config;

import dev.darcosse.common.cobblemonmarks.config.condition.KillCondition;
import dev.darcosse.common.cobblemonmarks.config.condition.MarkCondition;
import java.util.List;

public class Conditions {
    private final MarkCondition killCondition; // MarkCondition au lieu de KillCondition
    private final List<MarkCondition> required;
    private final List<MarkCondition> excluded;

    // MarkCondition (KillCondition ou CatchCondition) + required + excluded
    public Conditions(MarkCondition killCondition, List<MarkCondition> required,
                      List<MarkCondition> excluded) {
        this.killCondition = killCondition;
        this.required = required;
        this.excluded = excluded;
    }

    // MarkCondition + required, sans excluded
    public Conditions(MarkCondition killCondition, List<MarkCondition> required) {
        this(killCondition, required, List.of());
    }

    // MarkCondition seule, sans required ni excluded
    public Conditions(MarkCondition killCondition) {
        this(killCondition, List.of(), List.of());
    }

    // Sans killCondition + required + excluded
    public Conditions(List<MarkCondition> required, List<MarkCondition> excluded) {
        this(null, required, excluded);
    }

    // Sans killCondition + required seulement
    public Conditions(List<MarkCondition> required) {
        this(null, required, List.of());
    }

    public MarkCondition getKillCondition()        { return killCondition; }
    public List<MarkCondition> getRequired()       { return required; }
    public List<MarkCondition> getExcluded()       { return excluded; }
}