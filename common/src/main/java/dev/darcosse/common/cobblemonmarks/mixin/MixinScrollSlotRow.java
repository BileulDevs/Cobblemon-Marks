package dev.darcosse.common.cobblemonmarks.mixin;

import com.cobblemon.mod.common.client.gui.summary.widgets.screens.marks.MarksScrollingWidget;
import dev.darcosse.common.cobblemonmarks.util.IScrollSlotRow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin for the inner ScrollSlotRow class of the MarksScrollingWidget.
 * This class handles the actual rendering of the mark icons within the scrolling list.
 * It is responsible for drawing "locked" marks as darkened placeholders.
 *
 * @author Darcosse
 * @version 1.1
 * @since 2026
 */
@Mixin(value = MarksScrollingWidget.ScrollSlotRow.class, remap = false)
public abstract class MixinScrollSlotRow implements IScrollSlotRow {

    @Shadow public List markList;
    @Shadow public int x;
    @Shadow public int y;

    /** Stores the ResourceLocations of unowned marks in the order they appear as 'null' in the markList. */
    private List<ResourceLocation> cobblemonmarks$unownedMarkTextures = new java.util.ArrayList<>();

    /** Tracks which null slot (if any) is currently being hovered by the mouse. */
    private int cobblemonmarks$hoveredNullIndex = -1;

    @Override
    public int cobblemonmarks$getHoveredNullIndex() {
        return cobblemonmarks$hoveredNullIndex;
    }

    @Override
    public List cobblemonmarks$getMarkList() {
        return markList;
    }

    @Override
    public void cobblemonmarks$setUnownedMarkTextures(List<ResourceLocation> textures) {
        this.cobblemonmarks$unownedMarkTextures = textures;
    }

    /**
     * Injects rendering logic at the end of the row's render method.
     * This draws the darkened icons for unowned marks in slots where markList contains null.
     */
    @Inject(method = "renderRow", at = @At("TAIL"))
    private void cobblemonmarks$renderDisabledMarks(
            GuiGraphics context, int y, int x, int mouseX, int mouseY,
            CallbackInfoReturnable<Boolean> cir) {

        cobblemonmarks$hoveredNullIndex = -1;
        int horizontalSpacing = 3;
        int nullCount = 0;

        for (int index = 0; index < markList.size(); index++) {
            // Skip slots that contain actual owned Marks (vanilla Cobblemon renders these)
            if (markList.get(index) != null) continue;

            // Calculate position identical to vanilla slot positioning
            int startPosX = x + ((horizontalSpacing + 16) * index);
            int startPosY = y + 3;

            // Simple collision check for tooltips
            boolean hovered = mouseX >= startPosX && mouseX <= startPosX + 16
                    && mouseY >= startPosY && mouseY <= startPosY + 16;

            if (hovered) cobblemonmarks$hoveredNullIndex = index;

            // Only render if we have a assigned texture for this specific null slot
            if (nullCount < cobblemonmarks$unownedMarkTextures.size()) {
                ResourceLocation texture = cobblemonmarks$unownedMarkTextures.get(nullCount);

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                /* * VISUAL FEEDBACK LOGIC:
                 * brightness: 0.30f (very dark) -> 0.55f (semi-dark on hover)
                 * alpha: 0.6f (translucent) -> 0.85f (near opaque on hover)
                 */
                float brightness = hovered ? 0.55f : 0.30f;
                RenderSystem.setShaderColor(brightness, brightness, brightness, hovered ? 0.85f : 0.6f);

                // Draw the 16x16 icon
                context.blit(texture, startPosX, startPosY, 0, 0, 16, 16, 16, 16);

                // Reset color buffer to avoid tinting subsequent UI elements
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }

            nullCount++;
        }
    }
}