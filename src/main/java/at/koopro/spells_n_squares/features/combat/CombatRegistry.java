package at.koopro.spells_n_squares.features.combat;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.combat.block.DuelArenaBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for combat feature blocks.
 */
public class CombatRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpellsNSquares.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, DuelArenaBlock> DUEL_ARENA = RegistryHelper.registerBlockWithItem(
            BLOCKS, ITEMS,
            "duel_arena",
            id -> new DuelArenaBlock(RegistryHelper.createBlockProperties(id).strength(3.0f)));
    
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}







