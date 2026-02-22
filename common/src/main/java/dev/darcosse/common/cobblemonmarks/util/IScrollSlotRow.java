package dev.darcosse.common.cobblemonmarks.util;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

/**
 * Interface injected into Cobblemon's ScrollSlotRow via Mixin.
 * This acts as a bridge to manage unowned mark textures and track user interaction
 * with empty (null) slots in the Marks summary UI.
 *
 * @author Darcosse
 * @version 1.0
 * @since 2026
 */
public interface IScrollSlotRow {

    /**
     * Retrieves the index of the null slot currently under the player's mouse.
     * Used by the parent widget to determine which unowned mark's tooltip to display.
     * * @return The index of the hovered null slot, or -1 if none.
     */
    int cobblemonmarks$getHoveredNullIndex();

    /**
     * Accessor for the underlying mark list within the row.
     * Allows the Mixin logic to iterate through both owned (Mark) and unowned (null) entries.
     * * @return The raw list of marks/nulls in this specific row.
     */
    List cobblemonmarks$getMarkList();

    /**
     * Assigns the specific textures for unowned marks that should be rendered in
     * the 'null' slots of this row.
     * * @param textures A list of ResourceLocations pointing to the mark icons.
     */
    void cobblemonmarks$setUnownedMarkTextures(List<ResourceLocation> textures);
}