package at.koopro.spells_n_squares.core.data;

import at.koopro.spells_n_squares.features.playerclass.data.PlayerClassData;
import at.koopro.spells_n_squares.features.wand.WandData;
import at.koopro.spells_n_squares.modules.magic.internal.AnimagusData;
import at.koopro.spells_n_squares.modules.magic.internal.PatronusData;
import at.koopro.spells_n_squares.modules.spell.internal.SpellData;
import at.koopro.spells_n_squares.modules.tutorial.internal.TutorialData;
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
     * - Player class data
     * - Wand data (core, wood, attunement status)
     * - Tutorial data (tutorial progress)
     * - Animagus data (form, registration info)
     * - Patronus data (form, discovery info)
     * 
     * Additional modules will be added as they migrate.
     */
    public record PlayerData(
        SpellData spells,
        PlayerClassData.PlayerClassComponent classes,
        WandData.WandDataComponent wandData,
        TutorialData tutorial,
        AnimagusData animagus,
        PatronusData patronus
    ) {
        public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                SpellData.CODEC
                    .optionalFieldOf("spells", SpellData.empty())
                    .forGetter(PlayerData::spells),
                PlayerClassData.PlayerClassComponent.CODEC
                    .optionalFieldOf("classes", PlayerClassData.PlayerClassComponent.createDefault())
                    .forGetter(PlayerData::classes),
                WandData.WandDataComponent.CODEC
                    .optionalFieldOf("wandData", new WandData.WandDataComponent("", "", false))
                    .forGetter(PlayerData::wandData),
                TutorialData.CODEC
                    .optionalFieldOf("tutorial", TutorialData.empty())
                    .forGetter(PlayerData::tutorial),
                AnimagusData.CODEC
                    .optionalFieldOf("animagus", AnimagusData.empty())
                    .forGetter(PlayerData::animagus),
                PatronusData.CODEC
                    .optionalFieldOf("patronus", PatronusData.empty())
                    .forGetter(PlayerData::patronus)
            ).apply(instance, PlayerData::new)
        );
        
        /**
         * Creates default empty player data.
         */
        public static PlayerData empty() {
            return new PlayerData(
                SpellData.empty(),
                PlayerClassData.PlayerClassComponent.createDefault(),
                new WandData.WandDataComponent("", "", false),
                TutorialData.empty(),
                AnimagusData.empty(),
                PatronusData.empty()
            );
        }
    }
}

