package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Orchideous - A spell that conjures a bouquet of flowers.
 */
public class OrchideousSpell implements Spell {
    
    private static final int COOLDOWN = 20; // 1 second
    
    @Override
    public Identifier getId() {
        return SpellRegistry.spellId("orchideous");
    }
    
    @Override
    public String getName() {
        return "Orchideous";
    }
    
    @Override
    public String getDescription() {
        return "Conjures a bouquet of flowers";
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
        
        Vec3 pos = player.position().add(player.getLookAngle().scale(1.5));
        pos = pos.add(0, 0.5, 0);
        
        // Spawn flowers
        ItemStack flowers = new ItemStack(Items.POPPY, 3);
        ItemEntity flowerEntity = new ItemEntity(level, pos.x, pos.y, pos.z, flowers);
        flowerEntity.setPickUpDelay(10);
        level.addFreshEntity(flowerEntity);
        
        // Visual effect
        serverLevel.sendParticles(
            ParticleTypes.HAPPY_VILLAGER,
            pos.x, pos.y, pos.z,
            20,
            0.3, 0.3, 0.3,
            0.05
        );
        
        // Audio feedback
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.6f, 1.2f);
        
        return true;
    }
    
    @Override
    public float getVisualEffectIntensity() {
        return 0.3f;
    }
}

