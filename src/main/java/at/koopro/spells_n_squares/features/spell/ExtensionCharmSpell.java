package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Extension Charm spell for expanding interior space.
 */
public class ExtensionCharmSpell implements Spell {
    
    private static final int COOLDOWN_BASE = 100; // 5 seconds
    private static final int EXPANSION_RADIUS = 3; // blocks
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("extension_charm");
    }
    
    @Override
    public String getName() {
        return "Extension Charm";
    }
    
    @Override
    public String getDescription() {
        return "Expands the interior space of a room";
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
        
        // Get block player is looking at
        BlockHitResult hitResult = (BlockHitResult) player.pick(20.0, 1.0f, false);
        BlockPos targetPos = hitResult.getBlockPos();
        
        // Apply extension charm (simplified - would create expanded space in full implementation)
        applyExtensionCharm(serverLevel, targetPos, serverPlayer);
        
        return true;
    }
    
    /**
     * Applies the extension charm effect.
     */
    private void applyExtensionCharm(ServerLevel level, BlockPos center, ServerPlayer player) {
        // Simplified: create a magical effect
        // Full implementation would create an expanded interior dimension
        
        Vec3 centerVec = Vec3.atCenterOf(center);
        level.sendParticles(ParticleTypes.ENCHANT, centerVec.x, centerVec.y, centerVec.z,
            100, EXPANSION_RADIUS, EXPANSION_RADIUS, EXPANSION_RADIUS, 0.1);
        
        // Place some magical blocks as visual indicator
        for (int x = -EXPANSION_RADIUS; x <= EXPANSION_RADIUS; x++) {
            for (int z = -EXPANSION_RADIUS; z <= EXPANSION_RADIUS; z++) {
                if (x * x + z * z <= EXPANSION_RADIUS * EXPANSION_RADIUS) {
                    BlockPos pos = center.offset(x, 0, z);
                    if (level.getBlockState(pos).isAir()) {
                        // Place temporary magical marker (would be removed in full implementation)
                        level.setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        player.sendSystemMessage(Component.literal("Extension Charm applied!"));
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.8f;
    }
}

