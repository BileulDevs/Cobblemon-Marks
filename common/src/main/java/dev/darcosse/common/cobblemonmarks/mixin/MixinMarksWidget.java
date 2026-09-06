package dev.darcosse.common.cobblemonmarks.mixin;

import com.cobblemon.mod.common.api.mark.Mark;
import com.cobblemon.mod.common.api.mark.Marks;
import com.cobblemon.mod.common.client.gui.summary.widgets.screens.marks.MarksScrollingWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.screens.marks.MarksWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.client.MarkConditionDescriber;
import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import dev.darcosse.common.cobblemonmarks.util.IScrollSlotRow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixin for the MarksWidget to display unowned Marks and their requirements.
 * It injects logic to show greyed-out (locked) icons and custom tooltips
 * within the Pokémon Summary screen.
 *
 * @author Darcosse
 * @version 1.1
 * @since 2026
 */
@Mixin(value = MarksWidget.class, remap = false)
public abstract class MixinMarksWidget {

    @Shadow private Mark hoveredMark;
    @Shadow private MarksScrollingWidget marksScrollList;
    @Shadow private Pokemon pokemon;

    /** Tracks mark count to detect when a refresh is needed (e.g., after earning a mark). */
    private int cobblemonmarks$lastMarkCount = -1;

    /** Stores conditions for marks currently shown as unowned in the UI list. */
    private final List<MarksCondition> cobblemonmarks$unownedConditions = new ArrayList<>();

    /**
     * Post-constructor injection to populate the marks list with unowned placeholders.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void cobblemonmarks$onInit(int pX, int pY, Pokemon pokemon, CallbackInfo ci) {
        cobblemonmarks$refresh();
    }

    /**
     * Renders custom tooltips when the player hovers over a locked Mark slot.
     */
    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void cobblemonmarks$onRender(GuiGraphics context, int mouseX, int mouseY,
                                         float partialTicks, CallbackInfo ci) {

        if (!marksScrollList.isMouseOver(mouseX, mouseY)) return;

        // If an owned mark is hovered, let the original Cobblemon logic handle it.
        if (hoveredMark != null) return;

        int globalNullIndex = 0;

        for (int i = 0; i < marksScrollList.children().size(); i++) {
            IScrollSlotRow row = (IScrollSlotRow)(Object) marksScrollList.children().get(i);
            List rowMarks = row.cobblemonmarks$getMarkList();
            int hoveredNullIndex = row.cobblemonmarks$getHoveredNullIndex();

            if (hoveredNullIndex >= 0) {
                int localNullIndex = globalNullIndex;
                for (int j = 0; j < hoveredNullIndex; j++) {
                    if (rowMarks.get(j) == null) localNullIndex++;
                }

                if (localNullIndex < cobblemonmarks$unownedConditions.size()) {
                    MarksCondition markCondition = cobblemonmarks$unownedConditions.get(localNullIndex);
                    ResourceLocation markId = ResourceLocation.parse(markCondition.getMarkIdentifier());
                    Mark mark = Marks.getByIdentifier(markId);

                    List<Component> tooltip = new ArrayList<>();
                    if (mark != null) {
                        String markName = mark.getSerializedName().replace("cobblemon:", "");
                        MutableComponent markNameComponent = Component.translatable("cobblemon.mark." + markName);

                        // Add locked icon and mark name
                        tooltip.add(Component.literal("§8§l🔒 §r").append(markNameComponent));

                        // Add Mark Title (e.g., "The Early Bird") if available
                        Component title = mark.getTitle(Component.literal(""));
                        if (title != null) {
                            int color = (int) Long.parseLong(mark.getTitleColour(), 16);
                            tooltip.add(Component.literal(title.getString().trim())
                                    .withStyle(s -> s.withItalic(true).withColor(color)));
                        }
                    }

                    // Add dynamically generated requirement descriptions and progress bars
                    tooltip.addAll(MarkConditionDescriber.describe(markCondition, pokemon));
                    context.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
                }
                return;
            }

            for (Object m : rowMarks) {
                if (m == null) globalNullIndex++;
            }
        }
    }

    /**
     * Checks at the start of every render frame if the UI needs a refresh due to mark count changes.
     */
    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void cobblemonmarks$checkRefresh(GuiGraphics context, int mouseX, int mouseY,
                                             float partialTicks, CallbackInfo ci) {
        int currentCount = pokemon.getMarks().size();
        if (currentCount != cobblemonmarks$lastMarkCount) {
            cobblemonmarks$lastMarkCount = currentCount;
            cobblemonmarks$refresh();
        }
    }

    /**
     * Rebuilds the scroll list, merging owned marks with unowned placeholders.
     * Manages texture distribution for locked icons across ScrollSlotRows.
     */
    private void cobblemonmarks$refresh() {
        cobblemonmarks$unownedConditions.clear();

        // Start with obtained marks sorted alphabetically
        List<Mark> fullList = new ArrayList<>(pokemon.getMarks()
                .stream()
                .sorted((a, b) -> a.getIdentifier().toString().compareTo(b.getIdentifier().toString()))
                .toList());

        List<ResourceLocation> allUnownedTextures = new ArrayList<>();

        // Append 'null' entries for every mark defined in config that hasn't been obtained yet
        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            ResourceLocation markId = ResourceLocation.parse(markCondition.getMarkIdentifier());
            Mark mark = Marks.getByIdentifier(markId);
            if (mark != null && !pokemon.getMarks().contains(mark)) {
                fullList.add(null);
                cobblemonmarks$unownedConditions.add(markCondition);

                String markName = mark.getSerializedName().replace("cobblemon:", "");
                allUnownedTextures.add(ResourceLocation.fromNamespaceAndPath("cobblemon",
                        "textures/gui/mark/" + markName + ".png"));
            }
        }

        // Pad the list to maintain the 6-slot grid layout
        while (fullList.size() % 6 != 0) fullList.add(null);

        marksScrollList.children().clear();
        marksScrollList.createEntries(fullList);

        // Map textures to the correct row slots via the IScrollSlotRow interface
        int textureOffset = 0;
        for (int i = 0; i < marksScrollList.children().size(); i++) {
            IScrollSlotRow row = (IScrollSlotRow)(Object) marksScrollList.children().get(i);
            List rowMarks = row.cobblemonmarks$getMarkList();
            List<ResourceLocation> rowTextures = new ArrayList<>();
            for (Object m : rowMarks) {
                if (m == null && textureOffset < allUnownedTextures.size()) {
                    rowTextures.add(allUnownedTextures.get(textureOffset));
                    textureOffset++;
                }
            }
            row.cobblemonmarks$setUnownedMarkTextures(rowTextures);
        }
    }
}