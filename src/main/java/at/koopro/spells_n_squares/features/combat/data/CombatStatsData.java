package at.koopro.spells_n_squares.features.combat.data;

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
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:combat_stats";
    
    /**
     * Gets combat stats for a player from their persistent data component.
     */
    public static CombatStatsComponent getCombatStats(Player player) {
        if (player == null) {
            return new CombatStatsComponent();
        }
        if (at.koopro.spells_n_squares.core.util.PlayerValidationUtils.isClientSide(player)) {
            // On client, return default (data syncs from server)
            return new CombatStatsComponent();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return new CombatStatsComponent();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return new CombatStatsComponent();
        }
        
        try {
            return CombatStatsComponent.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(new CombatStatsComponent());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load combat stats for player {}, using default", player.getName().getString(), e);
            return new CombatStatsComponent();
        }
    }
    
    /**
     * Sets combat stats for a player in their persistent data component.
     */
    public static void setCombatStats(Player player, CombatStatsComponent stats) {
        if (player == null) {
            return;
        }
        if (at.koopro.spells_n_squares.core.util.PlayerValidationUtils.isClientSide(player)) {
            return; // Only set on server
        }
        
        try {
            var result = CombatStatsComponent.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                stats
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save combat stats for player {}", player.getName().getString(), e);
        }
    }
}
















