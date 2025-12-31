package at.koopro.spells_n_squares.features.mail.block;

import at.koopro.spells_n_squares.core.registry.BaseBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for mail-related BlockEntity types.
 * Now uses BaseBlockEntityRegistry for common patterns.
 */
public class MailBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        BaseBlockEntityRegistry.createRegistry();
    
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MailboxBlockEntity>> MAILBOX_BLOCK_ENTITY;
    
    /**
     * Initializes the BlockEntityType for MailboxBlock.
     * Must be called after the block is registered.
     * @param mailboxBlock The mailbox block instance
     */
    public static void initializeMailboxBlockEntity(Block mailboxBlock) {
        MAILBOX_BLOCK_ENTITY = BaseBlockEntityRegistry.<MailboxBlockEntity>registerBlockEntityType(
            BLOCK_ENTITIES,
            "mailbox_block_entity",
            (BlockEntityType<MailboxBlockEntity> type, BlockPos pos, BlockState state) -> new MailboxBlockEntity(type, pos, state),
            mailboxBlock
        );
    }
}

