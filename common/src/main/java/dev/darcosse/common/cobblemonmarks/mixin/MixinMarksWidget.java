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
import net.minecraft.ChatFormatting;
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

@Mixin(value = MarksWidget.class, remap = false)
public abstract class MixinMarksWidget {

    @Shadow private Mark hoveredMark;
    @Shadow private MarksScrollingWidget marksScrollList;
    @Shadow private Pokemon pokemon;

    private int cobblemonmarks$lastMarkCount = -1;

    private final List<MarksCondition> cobblemonmarks$unownedConditions = new ArrayList<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cobblemonmarks$onInit(int pX, int pY, Pokemon pokemon, CallbackInfo ci) {
        cobblemonmarks$unownedConditions.clear();

        List<Mark> fullList = new ArrayList<>(pokemon.getMarks()
                .stream()
                .sorted((a, b) -> a.getIdentifier().toString().compareTo(b.getIdentifier().toString()))
                .toList());

        // Collecter les marks non obtenues avec leur texture
        List<ResourceLocation> allUnownedTextures = new ArrayList<>();

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
            ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
            Mark mark = Marks.getByIdentifier(markId);
            if (mark != null && !pokemon.getMarks().contains(mark)) {
                fullList.add(null);
                cobblemonmarks$unownedConditions.add(markCondition);
                // La texture Cobblemon pour les marks : assets/cobblemon/textures/gui/marks/<nom>.png
                String markName = mark.getSerializedName().replace("cobblemon:", "");
                allUnownedTextures.add(ResourceLocation.fromNamespaceAndPath("cobblemon",
                        "textures/gui/mark/" + markName + ".png"));
            }
        }

        while (fullList.size() % 6 != 0) fullList.add(null);

        marksScrollList.children().clear();
        marksScrollList.createEntries(fullList);

        // Distribuer les textures aux rows dans l'ordre
        // Chaque row contient 6 slots, on passe les textures correspondant aux nulls de chaque row
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

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void cobblemonmarks$onRender(GuiGraphics context, int mouseX, int mouseY,
                                         float partialTicks, CallbackInfo ci) {

        if (!marksScrollList.isMouseOver(mouseX, mouseY)) return;

        // Si une mark obtenue est hovérée → Cobblemon gère déjà son tooltip, on ne fait rien
        if (hoveredMark != null) return;

        // Chercher un slot null hovered
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
                    String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
                    ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
                    Mark mark = Marks.getByIdentifier(markId);

                    List<Component> tooltip = new ArrayList<>();
                    if (mark != null) {
                        String markName = mark.getSerializedName().replace("cobblemon:", "");
                        // Nom de la mark avec sa propre couleur
                        MutableComponent markNameComponent = Component.translatable("cobblemon.mark." + markName);
                        tooltip.add(Component.literal("§8§l🔒 §r").append(markNameComponent));

                        // Titre : getTitle retourne "NomMark titre", on veut juste le titre
                        // On passe un Component vide pour éviter la répétition du nom
                        Component title = mark.getTitle(Component.literal(""));
                        if (title != null) {
                            int color = (int) Long.parseLong(mark.getTitleColour(), 16);
                            tooltip.add(Component.literal(title.getString().trim())
                                    .withStyle(s -> s.withItalic(true).withColor(color)));
                        }
                    }
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

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void cobblemonmarks$checkRefresh(GuiGraphics context, int mouseX, int mouseY,
                                             float partialTicks, CallbackInfo ci) {
        int currentCount = pokemon.getMarks().size();
        if (currentCount != cobblemonmarks$lastMarkCount) {
            cobblemonmarks$lastMarkCount = currentCount;
            cobblemonmarks$refresh();
        }
    }

    private void cobblemonmarks$refresh() {
        cobblemonmarks$unownedConditions.clear();

        List<Mark> fullList = new ArrayList<>(pokemon.getMarks()
                .stream()
                .sorted((a, b) -> a.getIdentifier().toString().compareTo(b.getIdentifier().toString()))
                .toList());

        List<ResourceLocation> allUnownedTextures = new ArrayList<>();

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
            ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
            Mark mark = Marks.getByIdentifier(markId);
            if (mark != null && !pokemon.getMarks().contains(mark)) {
                fullList.add(null);
                cobblemonmarks$unownedConditions.add(markCondition);
                String markName = mark.getSerializedName().replace("cobblemon:", "");
                allUnownedTextures.add(ResourceLocation.fromNamespaceAndPath("cobblemon",
                        "textures/gui/mark/" + markName + ".png"));
            }
        }

        while (fullList.size() % 6 != 0) fullList.add(null);

        marksScrollList.children().clear();
        marksScrollList.createEntries(fullList);

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