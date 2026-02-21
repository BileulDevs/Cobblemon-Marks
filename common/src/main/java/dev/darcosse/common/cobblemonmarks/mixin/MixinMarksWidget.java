package dev.darcosse.common.cobblemonmarks.mixin;

import com.cobblemon.mod.common.api.mark.Mark;
import com.cobblemon.mod.common.api.mark.Marks;
import com.cobblemon.mod.common.client.gui.summary.widgets.screens.marks.MarksScrollingWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.screens.marks.MarksWidget;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.client.MarkConditionDescriber;
import dev.darcosse.common.cobblemonmarks.config.MarksCondition;
import dev.darcosse.common.cobblemonmarks.config.MarksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
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
    @Shadow @Final private Pokemon pokemon;

    // Stocke les MarksCondition non obtenues correspondant aux slots fantômes
    private final List<MarksCondition> cobblemonmarks$unownedConditions = new ArrayList<>();

    @Inject(method = "init", at = @At("TAIL"))
    private void cobblemonmarks$onInit(int pX, int pY, Pokemon pokemon, CallbackInfo ci) {
        cobblemonmarks$unownedConditions.clear();

        List<Mark> fullList = new ArrayList<>(pokemon.getMarks()
                .stream()
                .sorted((a, b) -> a.getIdentifier().toString().compareTo(b.getIdentifier().toString()))
                .toList());

        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
            ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
            Mark mark = Marks.getByIdentifier(markId);
            if (mark != null && !pokemon.getMarks().contains(mark)) {
                fullList.add(null);
                cobblemonmarks$unownedConditions.add(markCondition);
            }
        }

        while (fullList.size() % 6 != 0) fullList.add(null);

        // Vider via children() qui est accessible en Kotlin interop
        marksScrollList.children().clear();
        marksScrollList.createEntries(fullList);
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void cobblemonmarks$onRender(GuiGraphics context, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (hoveredMark == null) return;

        // Chercher si la mark hovérée a une condition dans notre config
        for (MarksCondition markCondition : MarksConfig.CONDITIONS) {
            String rawPath = markCondition.getMarkIdentifier().replace("cobblemon:", "");
            ResourceLocation markId = ResourceLocation.fromNamespaceAndPath("cobblemon", rawPath);
            Mark mark = Marks.getByIdentifier(markId);
            if (mark != null && mark.equals(hoveredMark)) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal("§6" + mark.getTitle()));
                tooltip.addAll(MarkConditionDescriber.describe(markCondition, pokemon));
                context.renderComponentTooltip(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
                return;
            }
        }
    }
}