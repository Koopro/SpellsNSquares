package at.koopro.spells_n_squares.block.enchantments;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Enchantment table block for applying enchantments to items and wands.
 */
public class EnchantmentTableBlock extends Block {
    
    public EnchantmentTableBlock(Properties properties) {
        super(properties);
    }
    
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            // TODO: Open enchantment GUI screen
            // - Create EnchantmentMenu and EnchantmentScreen classes
            // - Use MenuProvider pattern: serverPlayer.openMenu(new EnchantmentMenuProvider(pos))
            // - Display interface for selecting enchantments and applying them to items/wands
            // - Show cost in currency or experience
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.enchantment_table.description"));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Spawn magical particles
        if (random.nextFloat() < 0.15f && level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                center.x, center.y + 0.5, center.z,
                3, 0.3, 0.2, 0.3, 0.01);
        }
    }
}

