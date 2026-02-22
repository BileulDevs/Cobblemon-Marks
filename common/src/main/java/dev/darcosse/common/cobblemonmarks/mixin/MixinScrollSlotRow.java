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

@Mixin(value = MarksScrollingWidget.ScrollSlotRow.class, remap = false)
public abstract class MixinScrollSlotRow implements IScrollSlotRow {

    @Shadow public List markList;
    @Shadow public int x;
    @Shadow public int y;

    // Stocke la liste des ResourceLocation des marks non obtenues, dans l'ordre des nulls
    private List<ResourceLocation> cobblemonmarks$unownedMarkTextures = new java.util.ArrayList<>();

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

    @Inject(method = "renderRow", at = @At("TAIL"))
    private void cobblemonmarks$renderDisabledMarks(
            GuiGraphics context, int y, int x, int mouseX, int mouseY,
            CallbackInfoReturnable<Boolean> cir) {

        cobblemonmarks$hoveredNullIndex = -1;
        int horizontalSpacing = 3;
        int nullCount = 0;

        for (int index = 0; index < markList.size(); index++) {
            if (markList.get(index) != null) continue;

            int startPosX = x + ((horizontalSpacing + 16) * index);
            int startPosY = y + 3;

            boolean hovered = mouseX >= startPosX && mouseX <= startPosX + 16
                    && mouseY >= startPosY && mouseY <= startPosY + 16;

            if (hovered) cobblemonmarks$hoveredNullIndex = index;

            if (nullCount < cobblemonmarks$unownedMarkTextures.size()) {
                ResourceLocation texture = cobblemonmarks$unownedMarkTextures.get(nullCount);

                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                // Grisé : teinte sombre, plus lumineux si hovered
                float brightness = hovered ? 0.55f : 0.30f;
                RenderSystem.setShaderColor(brightness, brightness, brightness, hovered ? 0.85f : 0.6f);

                context.blit(texture, startPosX, startPosY, 0, 0, 16, 16, 16, 16);

                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }

            nullCount++;
        }
    }
}