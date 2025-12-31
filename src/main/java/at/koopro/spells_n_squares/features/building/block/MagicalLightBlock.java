package at.koopro.spells_n_squares.features.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Magical light block with customizable effects.
 */
public class MagicalLightBlock extends Block {
    
    public enum LightColor {
        WHITE, BLUE, GREEN, RED, PURPLE, GOLD
    }
    
    private final LightColor lightColor;
    
    public MagicalLightBlock(Properties properties, LightColor color) {
        super(properties.lightLevel(state -> 15)); // Full brightness
        this.lightColor = color;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (level.isClientSide()) {
            return;
        }
        
        // Spawn colored particles based on light color
        if (random.nextFloat() < 0.3f && level instanceof ServerLevel serverLevel) {
            Vec3 center = Vec3.atCenterOf(pos);
            ParticleOptions particleType = getParticleForColor(lightColor);
            
            serverLevel.sendParticles(particleType,
                center.x, center.y, center.z,
                2, 0.1, 0.1, 0.1, 0.01);
        }
    }
    
    /**
     * Gets the particle type for a light color.
     */
    private ParticleOptions getParticleForColor(LightColor color) {
        return switch (color) {
            case WHITE -> ParticleTypes.END_ROD;
            case BLUE -> ParticleTypes.ELECTRIC_SPARK;
            case GREEN -> ParticleTypes.ENCHANT;
            case RED -> ParticleTypes.FLAME;
            case PURPLE -> ParticleTypes.PORTAL;
            case GOLD -> ParticleTypes.TOTEM_OF_UNDYING;
        };
    }
}













