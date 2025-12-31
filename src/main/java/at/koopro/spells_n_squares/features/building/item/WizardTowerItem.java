package at.koopro.spells_n_squares.features.building.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Item for placing wizard towers.
 */
public class WizardTowerItem extends Item {
    
    public WizardTowerItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Level level = context.getLevel();
        Player player = context.getPlayer();
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        // Check if position is valid for tower placement
        if (!canPlaceTower(serverLevel, pos)) {
            serverPlayer.sendSystemMessage(Component.literal("Cannot place tower here - needs solid ground"));
            return InteractionResult.FAIL;
        }
        
        // Place tower structure (simplified - would use structure system in full implementation)
        placeTowerStructure(serverLevel, pos);
        
        // Visual effect
        Vec3 center = Vec3.atCenterOf(pos);
        serverLevel.sendParticles(ParticleTypes.ENCHANT, center.x, center.y, center.z,
            50, 2.0, 2.0, 2.0, 0.1);
        
        // Consume item
        ItemStack stack = context.getItemInHand();
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Checks if a tower can be placed at the given position.
     */
    private boolean canPlaceTower(Level level, BlockPos pos) {
        // Check if ground is solid
        BlockPos ground = pos.below();
        return !level.getBlockState(ground).isAir() &&
               level.getBlockState(pos).isAir() &&
               level.getBlockState(pos.above()).isAir();
    }
    
    /**
     * Places a tower structure (simplified version).
     */
    private void placeTowerStructure(ServerLevel level, BlockPos basePos) {
        // Simplified: place a few blocks as a placeholder
        // Full implementation would use StructureTemplate or similar
        for (int y = 0; y < 5; y++) {
            BlockPos pos = basePos.above(y);
            if (y == 0 || y == 4) {
                level.setBlock(pos, Blocks.STONE_BRICKS.defaultBlockState(), 3);
            } else {
                level.setBlock(pos, Blocks.GLASS.defaultBlockState(), 3);
            }
        }
    }
}

