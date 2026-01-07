package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.features.wand.core.WandData;
import at.koopro.spells_n_squares.services.spell.internal.SpellData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Centralized player data component.
 * All player data is stored in a single data component for consistency.
 * 
 * Note: This is a foundation structure. Individual modules will contribute
 * their data structures as they are migrated.
 */
public final class PlayerDataComponent {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "spells_n_squares");
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PlayerData>> TYPE =
        DATA_COMPONENTS.register(
            "player_data",
            () -> DataComponentType.<PlayerData>builder()
                .persistent(PlayerData.CODEC)
                .build()
        );
    
    /**
     * Complete player data record.
     * All modules contribute their data here.
     * 
     * Currently includes:
     * - Spell data (slots, learned spells, cooldowns, active hold spell)
     * - Wand data (core, wood, attunement status)
     * - Identity data (blood status, magical race/type)
     */
    public record PlayerData(
        SpellData spells,
        WandData.WandDataComponent wandData,
        PlayerIdentityData.IdentityData identity
    ) {
        public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                SpellData.CODEC
                    .optionalFieldOf("spells", SpellData.empty())
                    .forGetter(PlayerData::spells),
                WandData.WandDataComponent.CODEC
                    .optionalFieldOf("wandData", new WandData.WandDataComponent("", "", false))
                    .forGetter(PlayerData::wandData),
                PlayerIdentityData.IdentityData.CODEC
                    .optionalFieldOf("identity", PlayerIdentityData.IdentityData.empty())
                    .forGetter(PlayerData::identity)
            ).apply(instance, PlayerData::new)
        );
        
        /**
         * Creates default empty player data.
         */
        public static PlayerData empty() {
            return new PlayerData(
                SpellData.empty(),
                new WandData.WandDataComponent("", "", false),
                PlayerIdentityData.IdentityData.empty()
            );
        }
        
        /**
         * Creates default player data based on player gender.
         */
        public static PlayerData defaultForGender(boolean isMale) {
            return new PlayerData(
                SpellData.empty(),
                new WandData.WandDataComponent("", "", false),
                PlayerIdentityData.IdentityData.defaultForGender(isMale)
            );
        }
    }
}

