package at.koopro.spells_n_squares.features.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing combat statistics.
 */
public final class CombatStatsData {
    private CombatStatsData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CombatStatsComponent>> COMBAT_STATS =
        DATA_COMPONENTS.register(
            "combat_stats",
            () -> DataComponentType.<CombatStatsComponent>builder()
                .persistent(CombatStatsComponent.CODEC)
                .build()
        );
    
    /**
     * Data component for combat statistics.
     */
    public record CombatStatsComponent(
        float accuracy,
        float dodgeChance,
        float criticalHitChance,
        float spellResistance,
        int duelsWon,
        int duelsLost
    ) {
        public static final Codec<CombatStatsComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.fieldOf("accuracy").forGetter(CombatStatsComponent::accuracy),
                Codec.FLOAT.fieldOf("dodgeChance").forGetter(CombatStatsComponent::dodgeChance),
                Codec.FLOAT.fieldOf("criticalHitChance").forGetter(CombatStatsComponent::criticalHitChance),
                Codec.FLOAT.fieldOf("spellResistance").forGetter(CombatStatsComponent::spellResistance),
                Codec.INT.fieldOf("duelsWon").forGetter(CombatStatsComponent::duelsWon),
                Codec.INT.fieldOf("duelsLost").forGetter(CombatStatsComponent::duelsLost)
            ).apply(instance, CombatStatsComponent::new)
        );
        
        public CombatStatsComponent() {
            this(0.8f, 0.1f, 0.05f, 0.0f, 0, 0);
        }
        
        public CombatStatsComponent withAccuracy(float accuracy) {
            return new CombatStatsComponent(accuracy, dodgeChance, criticalHitChance, spellResistance, duelsWon, duelsLost);
        }
        
        public CombatStatsComponent withDodgeChance(float dodgeChance) {
            return new CombatStatsComponent(accuracy, dodgeChance, criticalHitChance, spellResistance, duelsWon, duelsLost);
        }
        
        public CombatStatsComponent withCriticalHitChance(float criticalHitChance) {
            return new CombatStatsComponent(accuracy, dodgeChance, criticalHitChance, spellResistance, duelsWon, duelsLost);
        }
    }
    
    // Static storage for player combat stats (UUID -> CombatStatsComponent)
    private static final java.util.Map<java.util.UUID, CombatStatsComponent> playerCombatStats = new java.util.HashMap<>();
    
    /**
     * Gets combat stats for a player.
     * TODO: Migrate to actual data component when player data components are fully implemented
     */
    public static CombatStatsComponent getCombatStats(Player player) {
        return playerCombatStats.computeIfAbsent(player.getUUID(), uuid -> new CombatStatsComponent());
    }
    
    /**
     * Sets combat stats for a player.
     * TODO: Migrate to actual data component when player data components are fully implemented
     */
    public static void setCombatStats(Player player, CombatStatsComponent stats) {
        if (!player.level().isClientSide()) {
            playerCombatStats.put(player.getUUID(), stats);
        }
    }
}









