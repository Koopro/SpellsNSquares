package at.koopro.spells_n_squares.features.storage.block;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

/**
 * Registry for storage-related BlockEntity types.
 */
public class StorageBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpellsNSquares.MODID);
    
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<NewtsCaseBlockEntity>> NEWTS_CASE_BLOCK_ENTITY;
    
    /**
     * Initializes the BlockEntityType for NewtsCaseBlock.
     * Must be called after the block is registered.
     */
    @SuppressWarnings("unchecked")
    public static void initializeNewtsCaseBlockEntity() {
        final var blockHolder = at.koopro.spells_n_squares.features.storage.StorageRegistry.NEWTS_CASE;
        
        final BlockEntityType<NewtsCaseBlockEntity>[] typeRef = new BlockEntityType[1];
        
        NEWTS_CASE_BLOCK_ENTITY = BLOCK_ENTITIES.register("newts_case_block_entity", () -> {
            var block = blockHolder.value();
            typeRef[0] = new BlockEntityType<>(
                (pos, state) -> new NewtsCaseBlockEntity(typeRef[0], pos, state),
                Set.of(block)
            );
            return typeRef[0];
        });
    }
}







