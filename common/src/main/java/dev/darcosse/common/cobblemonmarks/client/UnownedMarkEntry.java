package dev.darcosse.common.cobblemonmarks.client;

import dev.darcosse.common.cobblemonmarks.config.MarksCondition;

public class UnownedMarkEntry {
    public final MarksCondition markCondition;
    public final String description;
    public final int current;
    public final int required;

    public UnownedMarkEntry(MarksCondition markCondition, String description, int current, int required) {
        this.markCondition = markCondition;
        this.description = description;
        this.current = current;
        this.required = required;
    }
}