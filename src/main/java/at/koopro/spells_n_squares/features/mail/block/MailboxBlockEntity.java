package at.koopro.spells_n_squares.features.mail.block;

import at.koopro.spells_n_squares.features.mail.data.MailboxData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * BlockEntity for storing mailbox inventory per player.
 * Each mailbox can store mail for multiple players (shared mailbox).
 */
public class MailboxBlockEntity extends BlockEntity {
    // Map of player UUID to their mailbox inventory
    private final Map<UUID, MailboxData.MailboxComponent> playerMailboxes = new HashMap<>();
    private static final int DEFAULT_MAILBOX_SLOTS = 27;
    
    public MailboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    /**
     * Gets the mailbox inventory for a specific player.
     * Creates a default empty mailbox if the player doesn't have one yet.
     */
    public MailboxData.MailboxComponent getMailboxInventory(UUID playerId) {
        return playerMailboxes.computeIfAbsent(playerId, 
            k -> MailboxData.MailboxComponent.createDefault(DEFAULT_MAILBOX_SLOTS));
    }
    
    /**
     * Sets the mailbox inventory for a specific player.
     */
    public void setMailboxInventory(UUID playerId, MailboxData.MailboxComponent mailbox) {
        playerMailboxes.put(playerId, mailbox);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    /**
     * Gets the number of mail items for a specific player.
     */
    public int getMailCount(UUID playerId) {
        MailboxData.MailboxComponent mailbox = getMailboxInventory(playerId);
        return mailbox.getMailCount();
    }
    
    /**
     * Checks if a player's mailbox has space.
     */
    public boolean hasSpace(UUID playerId) {
        MailboxData.MailboxComponent mailbox = getMailboxInventory(playerId);
        return mailbox.hasSpace();
    }
    
    /**
     * Delivers mail to a player's mailbox.
     * @return true if the mail was successfully delivered
     */
    public boolean deliverMail(UUID playerId, net.minecraft.world.item.ItemStack mailItem) {
        MailboxData.MailboxComponent mailbox = getMailboxInventory(playerId);
        
        if (!mailbox.hasSpace()) {
            return false;
        }
        
        int emptySlot = mailbox.getFirstEmptySlot();
        if (emptySlot >= 0) {
            mailbox = mailbox.setMail(emptySlot, mailItem);
            setMailboxInventory(playerId, mailbox);
            return true;
        }
        
        return false;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        
        // Load player mailboxes
        playerMailboxes.clear();
        CompoundTag mailboxesTag = input.read("playerMailboxes", net.minecraft.nbt.CompoundTag.CODEC)
            .orElse(new CompoundTag());
        
        for (String key : mailboxesTag.keySet()) {
            try {
                UUID playerId = UUID.fromString(key);
                net.minecraft.nbt.Tag tag = mailboxesTag.get(key);
                if (tag instanceof CompoundTag mailboxTag) {
                    MailboxData.MailboxComponent mailbox = MailboxData.MailboxComponent.CODEC
                        .decode(NbtOps.INSTANCE, mailboxTag)
                        .result()
                        .map(pair -> pair.getFirst())
                        .orElse(MailboxData.MailboxComponent.createDefault(DEFAULT_MAILBOX_SLOTS));
                    playerMailboxes.put(playerId, mailbox);
                }
            } catch (IllegalArgumentException e) {
                // Invalid UUID, skip
            }
        }
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        
        // Save player mailboxes
        CompoundTag mailboxesTag = new CompoundTag();
        for (Map.Entry<UUID, MailboxData.MailboxComponent> entry : playerMailboxes.entrySet()) {
            String key = entry.getKey().toString();
            MailboxData.MailboxComponent mailbox = entry.getValue();
            CompoundTag mailboxTag = (CompoundTag) MailboxData.MailboxComponent.CODEC
                .encodeStart(NbtOps.INSTANCE, mailbox)
                .result()
                .orElse(new CompoundTag());
            mailboxesTag.put(key, mailboxTag);
        }
        output.store("playerMailboxes", net.minecraft.nbt.CompoundTag.CODEC, mailboxesTag);
    }
}

