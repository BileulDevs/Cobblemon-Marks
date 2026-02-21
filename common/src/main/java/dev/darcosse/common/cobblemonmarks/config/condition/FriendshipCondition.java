package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.darcosse.common.cobblemonmarks.CobblemonMarksMod;
import net.minecraft.server.level.ServerPlayer;

public class FriendshipCondition implements MarkCondition {
    private final int requiredFriendship;

    public FriendshipCondition(int requiredFriendship) {
        this.requiredFriendship = requiredFriendship;
    }

    @Override
    public boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player) {
        int friendship = triggerPokemon.getFriendship();
        CobblemonMarksMod.LOGGER.info("Friendship de {} : {}", triggerPokemon.getSpecies().getName(), friendship);
        return friendship >= requiredFriendship;
    }

    @Override
    public String getNbtKey() { return null; } // pas de compteur, vérification instantanée

    @Override
    public int getRequiredCount() { return 1; }

    public int getRequiredFriendship() { return requiredFriendship; }
}