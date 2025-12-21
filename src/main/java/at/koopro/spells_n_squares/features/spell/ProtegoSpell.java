package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.features.spell.entity.ShieldOrbEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Protego spell that creates a protective shield reducing incoming damage.
 */
public class ProtegoSpell implements Spell {
    
    private static final int COOLDOWN = 120; // 6 seconds
    private static final int EFFECT_DURATION = 160; // 8 seconds
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("protego");
    }
    
    @Override
    public String getName() {
        return "Protego";
    }
    
    @Override
    public String getDescription() {
        return "Creates a protective shield that reduces incoming damage";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        // Remove any existing shield for this player
        // Note: ShieldOrbEntity doesn't track owner yet, so we'll remove all shields near the player
        for (Entity entity : level.getEntitiesOfClass(
                ShieldOrbEntity.class, 
                player.getBoundingBox().inflate(5.0))) {
            entity.discard();
        }
        
        // Apply Resistance effect (reduces damage by 20% per level)
        // Level 2 = 40% damage reduction
        player.addEffect(new MobEffectInstance(
            MobEffects.RESISTANCE,
            EFFECT_DURATION,
            1,   // Level 2 (40% damage reduction)
            false, // Not ambient
            true,  // Show particles
            true   // Show icon
        ));
        
        // Add absorption hearts for extra protection (4 hearts = 8 HP)
        player.addEffect(new MobEffectInstance(
            MobEffects.ABSORPTION,
            EFFECT_DURATION,
            1,   // Level 2 (4 absorption hearts)
            false,
            true,
            true
        ));
        
        // Spawn the shield orb entity
        ShieldOrbEntity shield = new ShieldOrbEntity(
            at.koopro.spells_n_squares.core.registry.ModEntities.SHIELD_ORB.get(),
            level
        );
        Vec3 pos = player.position().add(0, player.getBbHeight() * 0, 0);
        shield.setPos(pos.x, pos.y, pos.z);
        serverLevel.addFreshEntity(shield);
        
        // Visual and audio feedback
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.5f);
        
        // Initial burst effect when shield is cast
        serverLevel.sendParticles(
            ParticleTypes.ELECTRIC_SPARK,
            pos.x, pos.y, pos.z,
            50, 1.5, 1.0, 1.5, 0.1
        );
        
        serverLevel.sendParticles(
            ParticleTypes.ENCHANT,
            pos.x, pos.y, pos.z,
            30, 1.2, 0.8, 1.2, 0.05
        );
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.6f;
    }
}
