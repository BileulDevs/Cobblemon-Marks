package dev.darcosse.common.cobblemonmarks.config.condition;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.level.ServerPlayer;

public interface MarkCondition {
    /**
     * Appelé pour vérifier si la condition est remplie au moment de l'événement.
     * @param triggerPokemon  le pokémon du joueur concerné (killer ou victime selon contexte)
     * @param targetPokemon   le pokémon sauvage impliqué (peut être null selon contexte)
     * @param player          le joueur
     */
    boolean isMet(Pokemon triggerPokemon, Pokemon targetPokemon, ServerPlayer player);

    /**
     * Retourne la clé NBT unique pour stocker le compteur de cette condition.
     * Null si la condition n'est pas un compteur (météo, biome = vérification instantanée).
     */
    String getNbtKey();

    /**
     * Retourne le nombre de fois requis pour valider (1 si instantané).
     */
    int getRequiredCount();
}