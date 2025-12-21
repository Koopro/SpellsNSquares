package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Item Recall spell for summoning nearby items.
 */
public class ItemRecallSpell implements Spell {
    
    private static final int COOLDOWN_BASE = 40; // 2 seconds
    private static final int RECALL_RADIUS = 32; // blocks
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("item_recall");
    }
    
    @Override
    public String getName() {
        return "Item Recall";
    }
    
    @Override
    public String getDescription() {
        return "Summons nearby dropped items to you";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN_BASE;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        
        Vec3 playerPos = player.position();
        AABB searchArea = new AABB(
            playerPos.x - RECALL_RADIUS, playerPos.y - RECALL_RADIUS, playerPos.z - RECALL_RADIUS,
            playerPos.x + RECALL_RADIUS, playerPos.y + RECALL_RADIUS, playerPos.z + RECALL_RADIUS
        );
        
        // Find nearby items
        var items = level.getEntitiesOfClass(ItemEntity.class, searchArea);
        
        if (items.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No items found nearby"));
            return false;
        }
        
        // Teleport items to player
        int recalled = 0;
        for (ItemEntity item : items) {
            // Get position before teleporting for visual effect
            Vec3 itemPos = item.position();
            
            item.teleportTo(playerPos.x, playerPos.y + 0.5, playerPos.z);
            recalled++;
            
            // Visual effect at original position
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                itemPos.x, itemPos.y, itemPos.z,
                5, 0.1, 0.1, 0.1, 0.05);
        }
        
        serverPlayer.sendSystemMessage(Component.literal("Recalled " + recalled + " items"));
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.6f;
    }
}

