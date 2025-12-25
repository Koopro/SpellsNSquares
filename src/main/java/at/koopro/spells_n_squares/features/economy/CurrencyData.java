package at.koopro.spells_n_squares.features.economy;

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
     */
    private static CurrencyDataComponent fromAmount(CurrencySystem.CurrencyAmount amount) {
        return new CurrencyDataComponent(amount.galleons(), amount.sickles(), amount.knuts());
    }
    
    // Static storage for player currency data (UUID -> CurrencyDataComponent)
    private static final java.util.Map<java.util.UUID, CurrencyDataComponent> playerCurrencyData = new java.util.HashMap<>();
    
    /**
     * Gets currency data for a player.
     * TODO: Migrate to actual data component when player data components are fully implemented
     */
    public static CurrencyDataComponent getCurrencyData(Player player) {
        return playerCurrencyData.computeIfAbsent(player.getUUID(), uuid -> new CurrencyDataComponent());
    }
    
    /**
     * Sets currency data for a player.
     * TODO: Migrate to actual data component when player data components are fully implemented
     */
    public static void setCurrencyData(Player player, CurrencyDataComponent data) {
        if (!player.level().isClientSide()) {
            playerCurrencyData.put(player.getUUID(), data);
        }
    }
}














