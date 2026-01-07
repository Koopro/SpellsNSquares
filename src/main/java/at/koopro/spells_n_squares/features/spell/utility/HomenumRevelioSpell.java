package at.koopro.spells_n_squares.features.spell.utility;

import at.koopro.spells_n_squares.core.config.Config;
import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.fx.ScreenEffectManager;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
 * Homenum Revelio - reveals nearby humanoids.
 * Applies the Glowing effect to nearby living entities so they are easier to see through walls.
 */
public class HomenumRevelioSpell implements Spell {

    private static final int BASE_COOLDOWN = 160; // 8 seconds at multiplier 1.0
    private static final double RADIUS = 24.0;
    private static final int GLOW_DURATION = 160; // 8 seconds

    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("homenum_revelio");
    }

    @Override
    public String getName() {
        return "Homenum Revelio";
    }

    @Override
    public String getDescription() {
        return "Reveals nearby living beings by making them glow through walls.";
    }

    @Override
    public int getCooldown() {
        double scaled = BASE_COOLDOWN * Config.getSpellCooldownMultiplier();
        return Math.max(40, (int) Math.round(scaled));
    }

    @Override
    public boolean cast(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        Vec3 center = player.position().add(0, player.getBbHeight() * 0.5, 0);
        AABB box = new AABB(
            center.x - RADIUS, center.y - RADIUS, center.z - RADIUS,
            center.x + RADIUS, center.y + RADIUS, center.z + RADIUS
        );

        var targets = level.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity != player && entity.isAlive() && !entity.isSpectator());

        if (targets.isEmpty()) {
            return false;
        }

        int durationTicks = (int) Math.round(GLOW_DURATION * Math.max(0.25, Config.getSpellCooldownMultiplier()));

        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(
                MobEffects.GLOWING,
                durationTicks,
                0,
                false,
                false,
                true
            ));
        }

        // Ring of particles around the player
        serverLevel.sendParticles(
            ParticleTypes.END_ROD,
            center.x, center.y, center.z,
            80,
            RADIUS * 0.2, 0.5, RADIUS * 0.2,
            0.1
        );
        serverLevel.sendParticles(
            ParticleTypes.WAX_ON,
            center.x, center.y, center.z,
            40,
            RADIUS * 0.15, 0.3, RADIUS * 0.15,
            0.05
        );

        level.playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.ALLAY_ITEM_GIVEN,
            SoundSource.PLAYERS,
            1.0f,
            1.2f
        );

        if (!level.isClientSide()) {
            player.displayClientMessage(
                Component.translatable("spell.spells_n_squares.homenum_revelio.found", targets.size()),
                true
            );
        }

        return true;
    }

    @Override
    public float getVisualEffectIntensity() {
        return 0.5f;
    }

    @Override
    public void spawnCastEffects(Player player, Level level, boolean success) {
        if (!success || !level.isClientSide()) {
            return;
        }

        // Use a softer flash for this utility spell
        ScreenEffectManager.triggerSpellFlash();
    }
}

