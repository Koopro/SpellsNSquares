package at.koopro.spells_n_squares.features.spell.combat;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.fx.patterns.SpellFxPatterns;
import at.koopro.spells_n_squares.core.registry.ParticleEffectRegistry;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.fx.system.PostProcessingManager;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Confringo - an explosive blasting curse.
 * Creates a short-range explosive burst that knocks back and burns nearby targets.
 */
public class ConfringoSpell implements Spell {

    private static final int BASE_COOLDOWN = 120; // 6 seconds at multiplier 1.0
    private static final double RANGE = 14.0;
    private static final double BLAST_RADIUS = 3.5;
    private static final int BURN_DURATION = 80; // 4 seconds

    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("confringo");
    }

    @Override
    public String getName() {
        return "Confringo";
    }

    @Override
    public String getDescription() {
        return "A blasting curse that explodes in front of the caster, knocking back and burning targets.";
    }

    @Override
    public int getCooldown() {
        // Respect global cooldown tuning while keeping integer ticks
        double scaled = BASE_COOLDOWN * Config.getSpellCooldownMultiplier();
        return Math.max(20, (int) Math.round(scaled));
    }

    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 center = eyePos.add(look.scale(RANGE));

        AABB blastBox = new AABB(
            center.x - BLAST_RADIUS, center.y - BLAST_RADIUS, center.z - BLAST_RADIUS,
            center.x + BLAST_RADIUS, center.y + BLAST_RADIUS, center.z + BLAST_RADIUS
        );

        var targets = level.getEntitiesOfClass(LivingEntity.class, blastBox,
            entity -> entity != player && entity.isAlive() && !entity.isSpectator());

        if (targets.isEmpty()) {
            return false;
        }

        // Apply knockback + fire + brief blindness to each target
        for (LivingEntity target : targets) {
            Vec3 dir = target.position().subtract(eyePos).normalize();
            Vec3 knockback = dir.scale(0.9).add(0, 0.4, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;

            // Light targets on fire; scale duration by damage multiplier but clamp
            double damageMult = Config.getSpellDamageMultiplier();
            int burnTicks = (int) Math.round(BURN_DURATION * Math.max(0.25, damageMult));
            burnTicks = Math.min(burnTicks, 200);
            // 20 ticks = 1 second
            target.setRemainingFireTicks(burnTicks);

            // Brief blindness to sell the blast impact (server-side effect)
            target.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                40,
                0,
                false,
                true,
                true
            ));
        }

        // Enhanced explosion effect using burst pattern
        SpellFxPatterns.burst()
            .center(center)
            .particle(ParticleTypes.EXPLOSION)
            .count(8)
            .radius(0.0)
            .speed(0.0)
            .play(serverLevel);
        
        SpellFxPatterns.burst()
            .center(center)
            .particle(ParticleTypes.FLAME)
            .count(80)
            .radius(BLAST_RADIUS * 0.6)
            .speed(0.05)
            .play(serverLevel);
        
        SpellFxPatterns.burst()
            .center(center)
            .particle(ParticleTypes.SMALL_FLAME)
            .count(40)
            .radius(BLAST_RADIUS * 0.4)
            .speed(0.04)
            .play(serverLevel);
        
        // Add fire embers rising from explosion
        ParticleEffectRegistry.ParticleEffectTemplate embersTemplate = 
            ParticleEffectRegistry.get(Identifier.fromNamespaceAndPath("spells_n_squares", "fire_embers"));
        if (embersTemplate != null) {
            embersTemplate.spawn(serverLevel, center, 2.0);
        }

        // Audio feedback
        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.GENERIC_EXPLODE,
            SoundSource.PLAYERS,
            1.0f,
            1.1f
        );

        return true;
    }

    @Override
    public float getVisualEffectIntensity() {
        return 0.9f;
    }

    @Override
    public void spawnCastEffects(Player player, Level level, boolean success) {
        if (!success || !level.isClientSide()) {
            return;
        }

        // Keep existing spell flash + shake behavior
        ScreenEffectManager.triggerSpellFlash();
        if (getVisualEffectIntensity() > 0.7f) {
            ScreenEffectManager.triggerShake(0.1f * getVisualEffectIntensity(), 12);
        }

        // Add a brief inverted-colors post-processed flash if shaders are enabled
        if (Config.areShaderEffectsEnabled()
            && PostProcessingManager.isPostProcessingShaderAvailable(PostProcessingManager.INVERTED_COLORS_POST_SHADER)) {
            PostProcessingManager.addEffect(
                PostProcessingManager.INVERTED_COLORS_POST_SHADER,
                0.8f,
                10
            );
        }
    }
}

