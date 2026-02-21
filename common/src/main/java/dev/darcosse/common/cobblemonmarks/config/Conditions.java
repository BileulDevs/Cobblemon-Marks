package dev.darcosse.common.cobblemonmarks.config;

import dev.darcosse.common.cobblemonmarks.config.condition.KillCondition;
import dev.darcosse.common.cobblemonmarks.config.condition.MarkCondition;
import java.util.List;

public class Conditions {
    private final KillCondition killCondition;
    private final List<MarkCondition> required;
    private final List<MarkCondition> excluded;

    // KillCondition + required + excluded
    public Conditions(KillCondition killCondition, List<MarkCondition> required,
                      List<MarkCondition> excluded) {
        this.killCondition = killCondition;
        this.required = required;
        this.excluded = excluded;
    }

    // KillCondition + required, sans excluded
    public Conditions(KillCondition killCondition, List<MarkCondition> required) {
        this(killCondition, required, List.of());
    }

    // KillCondition seule, sans required ni excluded
    public Conditions(KillCondition killCondition) {
        this(killCondition, List.of(), List.of());
    }

    // Sans KillCondition + required + excluded
    public Conditions(List<MarkCondition> required, List<MarkCondition> excluded) {
        this(null, required, excluded);
    }

    // Sans KillCondition + required seulement
    public Conditions(List<MarkCondition> required) {
        this(null, required, List.of());
    }

    public KillCondition getKillCondition()        { return killCondition; }
    public List<MarkCondition> getRequired()       { return required; }
    public List<MarkCondition> getExcluded()       { return excluded; }
}