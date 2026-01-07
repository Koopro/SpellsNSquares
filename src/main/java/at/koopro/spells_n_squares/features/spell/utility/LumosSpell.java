package at.koopro.spells_n_squares.features.spell.utility;

import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.registry.ModDataComponents;
import at.koopro.spells_n_squares.core.registry.ModSounds;
import at.koopro.spells_n_squares.core.registry.ModTags;
import at.koopro.spells_n_squares.core.util.player.PlayerItemUtils;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.entity.LightOrbEntity;
import at.koopro.spells_n_squares.features.spell.manager.LumosManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Lumos/Nox spell that toggles a magical light around the player.
 * When active and sneaking, shoots a light orb and turns off.
 */
public class LumosSpell implements Spell {
    
    private static final int COOLDOWN = 20; // 1 second
    
    @Override
    public Identifier getId() {
        return at.koopro.spells_n_squares.core.registry.SpellRegistry.spellId("lumos");
    }
    
    @Override
    public String getName() {
        return "Lumos / Nox";
    }
    
    @Override
    public String getDescription() {
        return "Toggles a magical light around you";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public boolean cast(Player player, Level level) {
        ItemStack wand = findWand(player);
        if (wand.isEmpty()) {
            return false;
        }
        
        boolean isActive = wand.getOrDefault(ModDataComponents.LUMOS_ACTIVE.get(), false);
        boolean isSneaking = player.isShiftKeyDown();
        
        // If lumos is active and player sneaks while casting: shoot light orb and turn off
        if (isActive && isSneaking && !level.isClientSide()) {
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle().normalize();
            LightOrbEntity orb = new LightOrbEntity(
                at.koopro.spells_n_squares.features.spell.manager.SpellEntityRegistry.LIGHT_ORB.get(),
                level
            );
            level.addFreshEntity(orb);
            
            // Turn off Lumos on the wand
            wand.set(ModDataComponents.LUMOS_ACTIVE.get(), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.NOX.value(), SoundSource.PLAYERS, 0.7f, 1.0f);
            return true;
        }
        
        // Default toggle lumos state
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            boolean currentlyActive = LumosManager.isLumosActive(player);
            LumosManager.toggleLumos(serverPlayer, wand);
            boolean newState = !currentlyActive;
            
            // Play appropriate sound
            var soundEvent = newState ? ModSounds.LUMOS.value() : ModSounds.NOX.value();
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                soundEvent, SoundSource.PLAYERS, 0.7f, 1.0f);
            
            // Visual feedback
            if (level instanceof ServerLevel serverLevel) {
                double x = player.getX();
                double y = player.getY() + 1.0;
                double z = player.getZ();

                if (newState) {
                    // Lumos: bright particles
                    ParticlePool.queueParticle(
                        serverLevel,
                        ParticleTypes.END_ROD,
                        new Vec3(x, y, z),
                        15, 0.5, 0.5, 0.5, 0.1
                    );
                    ParticlePool.queueParticle(
                        serverLevel,
                        ParticleTypes.ELECTRIC_SPARK,
                        new Vec3(x, y, z),
                        10, 0.3, 0.3, 0.3, 0.05
                    );
                } else {
                    // Nox: dark particles
                    ParticlePool.queueParticle(
                        serverLevel,
                        ParticleTypes.SMOKE,
                        new Vec3(x, y, z),
                        10, 0.3, 0.3, 0.3, 0.05
                    );
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.4f;
    }
    
    private ItemStack findWand(Player player) {
        return PlayerItemUtils.findHeldItemByTag(player, ModTags.WANDS).orElse(ItemStack.EMPTY);
    }
}

