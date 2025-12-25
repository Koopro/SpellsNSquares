package at.koopro.spells_n_squares.features.fx.block;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

/**
 * Registry for FX-related BlockEntity types.
 */
public class FxBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpellsNSquares.MODID);
    
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyBallBlockEntity>> ENERGY_BALL_BLOCK_ENTITY;
    
    /**
     * Initializes the BlockEntityType for EnergyBallBlock.
     * Must be called after the block is registered.
     */
    public static void initializeEnergyBallBlockEntity() {
        final var blockHolder = at.koopro.spells_n_squares.features.fx.FxRegistry.ENERGY_BALL;
        
        final BlockEntityType<EnergyBallBlockEntity>[] typeRef = new BlockEntityType[1];
        
        ENERGY_BALL_BLOCK_ENTITY = BLOCK_ENTITIES.register("energy_ball_block_entity", () -> {
            var block = blockHolder.value();
            typeRef[0] = new BlockEntityType<>(
                (pos, state) -> new EnergyBallBlockEntity(typeRef[0], pos, state),
                Set.of(block)
            );
            return typeRef[0];
        });
    }
}

