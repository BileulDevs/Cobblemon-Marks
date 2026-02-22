package dev.darcosse.common.cobblemonmarks.util;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IScrollSlotRow {
    int cobblemonmarks$getHoveredNullIndex();
    List cobblemonmarks$getMarkList();
    void cobblemonmarks$setUnownedMarkTextures(List<ResourceLocation> textures);
}