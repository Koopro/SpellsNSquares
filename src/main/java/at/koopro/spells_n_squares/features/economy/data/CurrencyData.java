package at.koopro.spells_n_squares.features.economy.data;

import at.koopro.spells_n_squares.features.economy.system.CurrencySystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Data component for storing player currency.
 */
public final class CurrencyData {
    private CurrencyData() {
    }
    
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CurrencyDataComponent>> CURRENCY_DATA =
        DATA_COMPONENTS.register(
            "currency_data",
            () -> DataComponentType.<CurrencyDataComponent>builder()
                .persistent(CurrencyDataComponent.CODEC)
                .build()
        );
    
    /**
     * Data component for currency amounts.
     */
    public record CurrencyDataComponent(int galleons, int sickles, int knuts) {
        public static final Codec<CurrencyDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("galleons").forGetter(CurrencyDataComponent::galleons),
                Codec.INT.fieldOf("sickles").forGetter(CurrencyDataComponent::sickles),
                Codec.INT.fieldOf("knuts").forGetter(CurrencyDataComponent::knuts)
            ).apply(instance, CurrencyDataComponent::new)
        );
        
        public CurrencyDataComponent() {
            this(0, 0, 0);
        }
        
        public CurrencyDataComponent add(int galleons, int sickles, int knuts) {
            int totalKnuts = CurrencySystem.toKnuts(this.galleons + galleons, this.sickles + sickles, this.knuts + knuts);
            CurrencySystem.CurrencyAmount amount = CurrencySystem.fromKnuts(totalKnuts);
            return new CurrencyDataComponent(amount.galleons(), amount.sickles(), amount.knuts());
        }
        
        public CurrencyDataComponent remove(int galleons, int sickles, int knuts) {
            int totalKnuts = CurrencySystem.toKnuts(this.galleons, this.sickles, this.knuts);
            int removeKnuts = CurrencySystem.toKnuts(galleons, sickles, knuts);
            CurrencySystem.CurrencyAmount amount = CurrencySystem.fromKnuts(Math.max(0, totalKnuts - removeKnuts));
            return new CurrencyDataComponent(amount.galleons(), amount.sickles(), amount.knuts());
        }
        
        public boolean hasEnough(int galleons, int sickles, int knuts) {
            int totalKnuts = CurrencySystem.toKnuts(this.galleons, this.sickles, this.knuts);
            int requiredKnuts = CurrencySystem.toKnuts(galleons, sickles, knuts);
            return totalKnuts >= requiredKnuts;
        }
    }
    
    /**
     * Helper to convert CurrencyAmount to CurrencyDataComponent.
     * @deprecated Currently unused - reserved for future use
     */
    @Deprecated
    @SuppressWarnings("unused")
    private static CurrencyDataComponent fromAmount(CurrencySystem.CurrencyAmount amount) {
        return new CurrencyDataComponent(amount.galleons(), amount.sickles(), amount.knuts());
    }
    
    private static final String PERSISTENT_DATA_KEY = "spells_n_squares:currency_data";
    
    /**
     * Gets currency data for a player from their persistent data component.
     */
    public static CurrencyDataComponent getCurrencyData(Player player) {
        if (player.level().isClientSide()) {
            // On client, return default (data syncs from server)
            return new CurrencyDataComponent();
        }
        
        var persistentData = player.getPersistentData();
        var tagOpt = persistentData.getCompound(PERSISTENT_DATA_KEY);
        
        if (tagOpt.isEmpty()) {
            return new CurrencyDataComponent();
        }
        
        var tag = tagOpt.get();
        if (tag.isEmpty()) {
            return new CurrencyDataComponent();
        }
        
        try {
            return CurrencyDataComponent.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                tag
            ).result().orElse(new CurrencyDataComponent());
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to load currency data for player {}, using default", player.getName().getString(), e);
            return new CurrencyDataComponent();
        }
    }
    
    /**
     * Sets currency data for a player in their persistent data component.
     */
    public static void setCurrencyData(Player player, CurrencyDataComponent data) {
        if (player.level().isClientSide()) {
            return; // Only set on server
        }
        
        try {
            var result = CurrencyDataComponent.CODEC.encodeStart(
                net.minecraft.nbt.NbtOps.INSTANCE,
                data
            );
            
            result.result().ifPresent(tag -> {
                player.getPersistentData().put(PERSISTENT_DATA_KEY, tag);
            });
        } catch (Exception e) {
            com.mojang.logging.LogUtils.getLogger().warn(
                "Failed to save currency data for player {}", player.getName().getString(), e);
        }
    }
}
















