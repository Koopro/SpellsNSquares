package at.koopro.spells_n_squares.features.enchantments.block;

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
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            // Open enchantment GUI screen
            serverPlayer.openMenu(new EnchantmentMenuProvider(pos));
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Menu provider for enchantment table GUI.
     */
    private static class EnchantmentMenuProvider implements net.minecraft.world.MenuProvider {
        private final net.minecraft.core.BlockPos tablePos;
        
        public EnchantmentMenuProvider(net.minecraft.core.BlockPos tablePos) {
            this.tablePos = tablePos;
        }
        
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.spells_n_squares.enchantment_table");
        }
        
        @org.jetbrains.annotations.Nullable
        @Override
        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId, 
                                                                           net.minecraft.world.entity.player.Inventory playerInventory, 
                                                                           net.minecraft.world.entity.player.Player player) {
            return new at.koopro.spells_n_squares.features.enchantments.EnchantmentMenu(containerId, playerInventory, tablePos);
        }
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



