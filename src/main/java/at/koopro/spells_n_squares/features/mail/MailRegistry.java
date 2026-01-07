package at.koopro.spells_n_squares.features.mail;

import at.koopro.spells_n_squares.core.registry.RegistryHelper;
import at.koopro.spells_n_squares.features.mail.block.MailBlockEntities;
import at.koopro.spells_n_squares.features.mail.block.MailboxBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for mail feature items and blocks.
 */
public class MailRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, 
        at.koopro.spells_n_squares.SpellsNSquares.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(
        at.koopro.spells_n_squares.SpellsNSquares.MODID);
    
    public static final DeferredHolder<Block, MailboxBlock> MAILBOX = BLOCKS.register(
        "mailbox",
        id -> new MailboxBlock(RegistryHelper.createBlockProperties(id).strength(2.5f))
    );
    
    public static final DeferredHolder<Item, BlockItem> MAILBOX_ITEM = ITEMS.register(
        "mailbox",
        id -> new BlockItem(MAILBOX.value(), RegistryHelper.createItemProperties(id))
    );
    
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        MailBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        MailBlockEntities.initializeMailboxBlockEntity();
    }
}

