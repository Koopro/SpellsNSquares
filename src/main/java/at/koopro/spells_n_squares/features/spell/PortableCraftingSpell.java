package at.koopro.spells_n_squares.features.spell;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Portable Crafting spell for wand-based crafting.
 */
public class PortableCraftingSpell implements Spell {
    
    private static final int COOLDOWN_BASE = 60; // 3 seconds
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("portable_crafting");
    }
    
    @Override
    public String getName() {
        return "Portable Crafting";
    }
    
    @Override
    public String getDescription() {
        return "Opens a portable crafting interface";
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
        
        // Visual effect
        Vec3 pos = player.position();
        serverLevel.sendParticles(ParticleTypes.ENCHANT, pos.x, pos.y + 1.0, pos.z,
            30, 0.3, 0.3, 0.3, 0.1);
        
        // Simplified: send message for now
        // Full implementation would open portable crafting GUI
        serverPlayer.sendSystemMessage(Component.literal("Portable Crafting - Craft anywhere!"));
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }
}

