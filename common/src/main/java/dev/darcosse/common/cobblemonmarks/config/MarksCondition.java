package dev.darcosse.common.cobblemonmarks.config;

public class MarksCondition {
    private final String markIdentifier;
    private final Conditions conditions;

    public MarksCondition(String markIdentifier, Conditions conditions) {
        this.markIdentifier = markIdentifier;
        this.conditions = conditions;
    }

    public String getMarkIdentifier()    { return markIdentifier; }
    public Conditions getConditions()    { return conditions; }
}