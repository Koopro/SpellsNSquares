package at.koopro.spells_n_squares.features.cloak;

import at.koopro.spells_n_squares.core.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Revealer Dust item that can break invisibility cloaks temporarily.
 */
public class RevealerDustItem extends Item {
    
    public RevealerDustItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        
        ItemStack stack = player.getItemInHand(hand);
        
        // Find nearby invisible players wearing cloaks
        AABB searchBox = new AABB(player.getX() - 16, player.getY() - 16, player.getZ() - 16,
                                  player.getX() + 16, player.getY() + 16, player.getZ() + 16);
        
        if (level instanceof ServerLevel serverLevel) {
            level.getEntitiesOfClass(Player.class, searchBox).forEach(target -> {
                if (target == player || !target.isInvisible()) {
                    return;
                }
                
                ItemStack chestArmor = target.getItemBySlot(EquipmentSlot.CHEST);
                boolean hasCloak = chestArmor.getItem() == ModItems.DEMIGUISE_CLOAK.get() ||
                                  chestArmor.getItem() == ModItems.DEATHLY_HALLOW_CLOAK.get();
                
                if (hasCloak) {
                    // Temporarily break the cloak (remove invisibility)
                    target.setInvisible(false);
                    
                    // Visual effect
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.GLOW,
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1
                    );
                }
            });
        }
        
        // Consume one dust
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
}
