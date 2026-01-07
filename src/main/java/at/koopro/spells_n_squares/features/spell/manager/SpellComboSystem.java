package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.core.data.PlayerDataHelper;
import at.koopro.spells_n_squares.core.util.collection.CollectionFactory;
import at.koopro.spells_n_squares.services.spell.internal.SpellData;
import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Manages spell combo detection and execution.
 * Tracks recent spell casts and triggers combo effects when patterns are matched.
 */
public final class SpellComboSystem {
    private SpellComboSystem() {
    }
    
    /**
     * Represents a spell combo pattern and its effect.
     */
    public static class SpellCombo {
        private final List<Identifier> pattern; // Sequence of spell IDs
        private final String name; // Combo name
        private final BiConsumer<Player, Level> effect; // Effect to execute
        private final float powerMultiplier; // Power multiplier for combo
        
        public SpellCombo(List<Identifier> pattern, String name, BiConsumer<Player, Level> effect, float powerMultiplier) {
            this.pattern = CollectionFactory.createListFrom(pattern);
            this.name = name;
            this.effect = effect;
            this.powerMultiplier = powerMultiplier;
        }
        
        public List<Identifier> getPattern() {
            return pattern;
        }
        
        public String getName() {
            return name;
        }
        
        public float getPowerMultiplier() {
            return powerMultiplier;
        }
        
        /**
         * Checks if this combo pattern matches the recent casts.
         */
        public boolean matches(List<Identifier> recentCasts) {
            if (recentCasts.size() < pattern.size()) {
                return false;
            }
            
            // Check if the last N casts match the pattern (in order)
            int startIndex = recentCasts.size() - pattern.size();
            for (int i = 0; i < pattern.size(); i++) {
                if (!recentCasts.get(startIndex + i).equals(pattern.get(i))) {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * Executes the combo effect.
         */
        public void execute(Player player, Level level) {
            if (effect != null) {
                effect.accept(player, level);
            }
        }
    }
    
    // Registered combos
    private static final List<SpellCombo> registeredCombos = CollectionFactory.createList();
    
    /**
     * Registers a spell combo pattern.
     */
    public static void registerCombo(SpellCombo combo) {
        if (combo != null && !registeredCombos.contains(combo)) {
            registeredCombos.add(combo);
        }
    }
    
    /**
     * Initializes default spell combos.
     */
    public static void initializeDefaultCombos() {
        // Fire + Lightning = Explosive Lightning
        registerCombo(new SpellCombo(
            List.of(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "incendio"),
                net.minecraft.resources.Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "lightning")
            ),
            "Explosive Lightning",
            (player, level) -> {
                if (level instanceof ServerLevel serverLevel) {
                    Vec3 pos = player.position();
                    // Create explosive lightning effect
                    serverLevel.explode(null, pos.x, pos.y + 1, pos.z, 3.0f, Level.ExplosionInteraction.NONE);
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                        pos.x, pos.y + 1, pos.z,
                        10, 1.0, 1.0, 1.0, 0.1
                    );
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(
                            net.minecraft.network.chat.Component.translatable("message.spells_n_squares.combo.explosive_lightning")
                        );
                    }
                }
            },
            1.5f
        ));
        
        // Heal + Protection = Regenerating Shield
        registerCombo(new SpellCombo(
            List.of(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "heal"),
                net.minecraft.resources.Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "protego")
            ),
            "Regenerating Shield",
            (player, level) -> {
                if (level instanceof ServerLevel) {
                    // Apply regeneration effect
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.REGENERATION,
                        200, // 10 seconds
                        1,
                        false,
                        true,
                        true
                    ));
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(
                            net.minecraft.network.chat.Component.translatable("message.spells_n_squares.combo.regenerating_shield")
                        );
                    }
                }
            },
            1.3f
        ));
        
        // Light + Fire = Bright Flames
        registerCombo(new SpellCombo(
            List.of(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "lumos"),
                net.minecraft.resources.Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "incendio")
            ),
            "Bright Flames",
            (player, level) -> {
                if (level instanceof ServerLevel serverLevel) {
                    Vec3 pos = player.position();
                    // Create bright flame effect
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.FLAME,
                        pos.x, pos.y + 1, pos.z,
                        30, 2.0, 2.0, 2.0, 0.05
                    );
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(
                            net.minecraft.network.chat.Component.translatable("message.spells_n_squares.combo.bright_flames")
                        );
                    }
                }
            },
            1.2f
        ));
    }
    
    /**
     * Records a spell cast and checks for combo matches.
     * Should be called after a spell is successfully cast.
     * 
     * @param player The player who cast the spell
     * @param spellId The spell that was cast
     * @param level The level
     * @return The combo that was triggered, or null if no combo matched
     */
    public static SpellCombo recordCastAndCheckCombo(Player player, Identifier spellId, Level level) {
        if (player == null || spellId == null || level == null || level.isClientSide()) {
            return null;
        }
        
        // Update recent casts in player data
        SpellData current = PlayerDataHelper.getSpellData(player);
        SpellData updated = current.withRecentCast(spellId);
        PlayerDataHelper.setSpellData(player, updated);
        
        // Check for combo matches
        List<Identifier> recentCasts = updated.recentCasts();
        for (SpellCombo combo : registeredCombos) {
            if (combo.matches(recentCasts)) {
                // Execute combo effect
                combo.execute(player, level);
                
                // Clear recent casts after combo (to prevent immediate re-triggering)
                SpellData cleared = updated.withoutRecentCasts();
                PlayerDataHelper.setSpellData(player, cleared);
                
                return combo;
            }
        }
        
        return null;
    }
    
    /**
     * Gets all registered combos.
     */
    public static List<SpellCombo> getRegisteredCombos() {
        return CollectionFactory.createListFrom(registeredCombos);
    }
}

