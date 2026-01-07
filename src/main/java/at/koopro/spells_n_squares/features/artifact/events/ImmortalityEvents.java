package at.koopro.spells_n_squares.features.artifact.events;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.artifact.ArtifactRegistry;
import at.koopro.spells_n_squares.features.artifact.ImmortalityHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles immortality system: god mode, withered state, and death prevention.
 * Now uses MobEffect for immortality instead of data components.
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class ImmortalityEvents {
    
    /**
     * Target max health for withered players (6 HP = 3 hearts).
     */
    private static final float WITHERED_MAX_HEALTH = 6.0f;
    
    /**
     * Identifier for the withered health modifier.
     * Used to identify and remove the modifier.
     */
    private static final net.minecraft.resources.Identifier WITHERED_HEALTH_MODIFIER_ID = 
        net.minecraft.resources.Identifier.fromNamespaceAndPath("spells_n_squares", "withered_health_cap");
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        
        if (player.level().isClientSide()) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Handle god mode for immortal players
            if (ImmortalityHelper.isImmortal(player)) {
            // --- GOD MODE ---
            // Lock hunger at 100%
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0f);
            
            // Rapid regeneration (1 HP per second)
            if (player.tickCount % 20 == 0) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0f);
                }
            }
            
            // Remove withered modifier if present
            removeWitheredModifier(player);
        } else if (ImmortalityHelper.isWithered(player)) {
            // --- WITHERED STATE ---
            // Max health capped at 3 hearts (6 HP)
            applyWitheredModifier(player);
            
            // Apply slowness
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SLOWNESS, 40, 1, false, false));
            
            // Disable natural regeneration (already handled by low max health)
        } else {
            // Remove withered modifier if player is not withered
            removeWitheredModifier(player);
        }
        }, "ticking player immortality", player);
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (player.level().isClientSide()) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Only prevent death if player is currently immortal (has active effect)
            if (ImmortalityHelper.isImmortal(player)) {
            // Prevent death
            event.setCanceled(true);
            
            // Remove immortality effect (they've "died" and are now withered)
            player.removeEffect(ArtifactRegistry.IMMORTALITY_EFFECT);
            ImmortalityHelper.markAsWithered(player);
            
            // Heal player to 1 HP (they're withered now)
            player.setHealth(1.0f);
            
            // Visual effects
            if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                net.minecraft.world.phys.Vec3 pos = new net.minecraft.world.phys.Vec3(
                    player.getX(), player.getY() + 1.0, player.getZ());
                ParticlePool.queueParticle(
                    serverLevel,
                    net.minecraft.core.particles.ParticleTypes.SMOKE,
                    pos,
                    30, 0.5, 0.5, 0.5, 0.1
                );
                ParticlePool.queueParticle(
                    serverLevel,
                    net.minecraft.core.particles.ParticleTypes.SOUL,
                    pos,
                    20, 0.3, 0.3, 0.3, 0.05
                );
            }
            
            player.level().playSound(null, player.blockPosition(),
                net.minecraft.sounds.SoundEvents.WITHER_DEATH,
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.8f);
        }
        }, "handling player death", player);
    }
    
    /**
     * Applies the withered health cap (6 HP max) using AttributeModifier.
     * Uses AttributeModifier to reduce max health to 6 HP.
     * Note: This implementation uses addPermanentModifier which persists across sessions.
     */
    private static void applyWitheredModifier(Player player) {
        var maxHealthAttribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (maxHealthAttribute == null) {
            return;
        }
        
        // Check if modifier already exists
        var existingModifier = maxHealthAttribute.getModifier(WITHERED_HEALTH_MODIFIER_ID);
        if (existingModifier != null) {
            return; // Already applied
        }
        
        // Calculate the modifier amount needed to cap at 6 HP
        // If base is 20, we need -14 to get to 6
        double currentBase = maxHealthAttribute.getBaseValue();
        double targetValue = WITHERED_MAX_HEALTH;
        double modifierAmount = targetValue - currentBase;
        
        // Create and apply the modifier
        // Constructor signature: (Identifier, double, Operation)
        net.minecraft.world.entity.ai.attributes.AttributeModifier modifier = 
            new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                WITHERED_HEALTH_MODIFIER_ID,
                modifierAmount,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
            );
        
        maxHealthAttribute.addPermanentModifier(modifier);
        
        // Cap current health if it exceeds the new max
        if (player.getHealth() > WITHERED_MAX_HEALTH) {
            player.setHealth(WITHERED_MAX_HEALTH);
        }
    }
    
    /**
     * Removes the withered health modifier.
     * Restores max health to its original value.
     */
    private static void removeWitheredModifier(Player player) {
        var maxHealthAttribute = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (maxHealthAttribute == null) {
            return;
        }
        
        // Remove the modifier if it exists
        var existingModifier = maxHealthAttribute.getModifier(WITHERED_HEALTH_MODIFIER_ID);
        if (existingModifier != null) {
            maxHealthAttribute.removeModifier(existingModifier);
        }
    }
    
    /**
     * Handles when the immortality effect expires.
     * Marks the player as withered.
     */
    @SubscribeEvent
    public static void onEffectExpire(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (player.level().isClientSide()) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Check if the expired effect is immortality
            if (event.getEffectInstance() != null && 
                event.getEffectInstance().getEffect() == ArtifactRegistry.IMMORTALITY_EFFECT.get()) {
                // Mark player as withered when immortality expires
                ImmortalityHelper.markAsWithered(player);
            }
        }, "handling effect expiration", player);
    }
}

