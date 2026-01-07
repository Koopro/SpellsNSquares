package at.koopro.spells_n_squares.features.economy.block;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

/**
 * Registry for economy-related BlockEntity types.
 */
public class EconomyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpellsNSquares.MODID);
    
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<VaultBlockEntity>> VAULT_BLOCK_ENTITY;
    
    /**
     * Initializes the BlockEntityType for VaultBlock.
     * Must be called after the block is registered.
     */
    @SuppressWarnings("unchecked")
    public static void initializeVaultBlockEntity() {
        final var blockHolder = at.koopro.spells_n_squares.features.economy.EconomyRegistry.VAULT;
        
        final BlockEntityType<VaultBlockEntity>[] typeRef = new BlockEntityType[1];
        
        VAULT_BLOCK_ENTITY = BLOCK_ENTITIES.register("vault_block_entity", () -> {
            var block = blockHolder.value();
            typeRef[0] = new BlockEntityType<>(
                (pos, state) -> new VaultBlockEntity(typeRef[0], pos, state),
                Set.of(block)
            );
            return typeRef[0];
        });
    }
}

