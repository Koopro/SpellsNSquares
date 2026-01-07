package at.koopro.spells_n_squares.features.enchantments.block;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

/**
 * Registry for enchantment-related BlockEntity types.
 */
public class EnchantmentBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpellsNSquares.MODID);
    
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantmentTableBlockEntity>> ENCHANTMENT_TABLE_BLOCK_ENTITY;
    
    /**
     * Initializes the BlockEntityType for EnchantmentTableBlock.
     * Must be called after the block is registered.
     */
    @SuppressWarnings("unchecked")
    public static void initializeEnchantmentTableBlockEntity() {
        final var blockHolder = at.koopro.spells_n_squares.features.enchantments.EnchantmentRegistry.ENCHANTMENT_TABLE;
        
        final BlockEntityType<EnchantmentTableBlockEntity>[] typeRef = new BlockEntityType[1];
        
        ENCHANTMENT_TABLE_BLOCK_ENTITY = BLOCK_ENTITIES.register("enchantment_table_block_entity", () -> {
            var block = blockHolder.value();
            typeRef[0] = new BlockEntityType<>(
                (pos, state) -> new EnchantmentTableBlockEntity(typeRef[0], pos, state),
                Set.of(block)
            );
            return typeRef[0];
        });
    }
}

