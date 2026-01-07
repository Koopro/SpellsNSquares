package at.koopro.spells_n_squares.features.mail;

import at.koopro.spells_n_squares.core.menu.BaseModMenu;
import at.koopro.spells_n_squares.core.registry.ModMenus;
import at.koopro.spells_n_squares.core.util.dev.DevLogger;
import at.koopro.spells_n_squares.core.util.math.MathUtils;
import at.koopro.spells_n_squares.core.util.math.PositionUtils;
import at.koopro.spells_n_squares.features.mail.block.MailboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the Mailbox block.
 * Allows players to view and retrieve received mail.
 */
public class MailboxMenu extends BaseModMenu {
    private final ContainerLevelAccess access;
    private final BlockPos mailboxPos;
    private static final int MAILBOX_SLOTS = 27; // 3 rows x 9 columns
    
    // Client-side constructor
    public MailboxMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        super(ModMenus.MAILBOX_MENU.get(), containerId, playerInventory);
        this.mailboxPos = buffer.readBlockPos();
        this.access = ContainerLevelAccess.NULL;
        DevLogger.logMethodEntry(this, "MailboxMenu", 
            "containerId=" + containerId + 
            ", mailboxPos=" + DevLogger.formatPos(mailboxPos) + " (client-side)");
        addCustomSlots();
        addPlayerSlots(playerInventory, 8, 84);
    }
    
    // Server-side constructor
    public MailboxMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.MAILBOX_MENU.get(), containerId, playerInventory);
        this.access = access;
        this.mailboxPos = access.evaluate((level, pos) -> pos).orElse(BlockPos.ZERO);
        DevLogger.logMethodEntry(this, "MailboxMenu", 
            "containerId=" + containerId + 
            ", mailboxPos=" + DevLogger.formatPos(mailboxPos) + " (server-side)");
        addCustomSlots();
        addPlayerSlots(playerInventory, 8, 84);
    }
    
    @Override
    protected void addCustomSlots() {
        // Mailbox slots (3 rows x 9 columns)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(getMailboxInventory(), col + row * 9, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        // Only allow mail items
                        // Note: Currently mail items are represented as paper with custom name.
                        // When a custom mail item type is implemented, check for that item type here.
                        if (stack.isEmpty()) {
                            return false;
                        }
                        // Temporary solution: check if item has custom name indicating it's mail
                        // This is a placeholder until custom mail item type is implemented
                        var customName = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
                        return customName != null && customName.getString().contains("Mail from");
                    }
                });
            }
        }
    }
    
    /**
     * Gets the mailbox inventory from the BlockEntity.
     */
    private net.minecraft.world.Container getMailboxInventory() {
        return access.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof MailboxBlockEntity blockEntity) {
                return blockEntity.getInventory();
            }
            // Fallback: return empty container if BlockEntity not found
            return new net.minecraft.world.SimpleContainer(MAILBOX_SLOTS);
        }).orElse(new net.minecraft.world.SimpleContainer(MAILBOX_SLOTS));
    }
    
    @Override
    protected boolean isValid(Player player) {
        DevLogger.logMethodEntry(this, "isValid", 
            "player=" + (player != null ? player.getName().getString() : "null") +
            ", mailboxPos=" + DevLogger.formatPos(mailboxPos));
        // Check if player is still near the mailbox
        if (mailboxPos == null) {
            DevLogger.logMethodExit(this, "isValid", false);
            return false;
        }
        boolean result = MathUtils.distanceSquared(player.position(), PositionUtils.toVec3(mailboxPos)) <= 64.0;
        DevLogger.logReturnValue(this, "isValid", result);
        return result;
    }
    
    @Override
    protected int getContainerStartSlot() {
        return 0;
    }
    
    @Override
    protected int getContainerEndSlot() {
        return MAILBOX_SLOTS;
    }
    
    @Override
    protected int getPlayerInventoryStartSlot() {
        return MAILBOX_SLOTS;
    }
    
    @Override
    protected int getPlayerInventoryEndSlot() {
        return MAILBOX_SLOTS + 36;
    }
    
    public BlockPos getMailboxPos() {
        return mailboxPos;
    }
}

