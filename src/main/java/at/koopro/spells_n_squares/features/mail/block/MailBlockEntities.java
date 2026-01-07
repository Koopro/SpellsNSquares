package at.koopro.spells_n_squares.features.mail.block;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

/**
 * Registry for mail-related BlockEntity types.
 */
public class MailBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpellsNSquares.MODID);
    
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MailboxBlockEntity>> MAILBOX_BLOCK_ENTITY;
    
    /**
     * Initializes the BlockEntityType for MailboxBlock.
     * Must be called after the block is registered.
     */
    @SuppressWarnings("unchecked")
    public static void initializeMailboxBlockEntity() {
        final var blockHolder = at.koopro.spells_n_squares.features.mail.MailRegistry.MAILBOX;
        
        final BlockEntityType<MailboxBlockEntity>[] typeRef = new BlockEntityType[1];
        
        MAILBOX_BLOCK_ENTITY = BLOCK_ENTITIES.register("mailbox_block_entity", () -> {
            var block = blockHolder.value();
            typeRef[0] = new BlockEntityType<>(
                (pos, state) -> new MailboxBlockEntity(typeRef[0], pos, state),
                Set.of(block)
            );
            return typeRef[0];
        });
    }
}

