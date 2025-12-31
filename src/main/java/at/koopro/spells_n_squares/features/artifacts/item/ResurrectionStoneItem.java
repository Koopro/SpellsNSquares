package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.features.artifacts.ResurrectionStoneDeathTracker;
import at.koopro.spells_n_squares.features.artifacts.data.ResurrectionStoneData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Resurrection Stone artifact that can summon shades of the dead.
 */
public class ResurrectionStoneItem extends Item {
    
    private static final int COOLDOWN_TICKS = 300; // 15 seconds
    private static final double SUMMON_RANGE = 8.0;
    
    public ResurrectionStoneItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }
        
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        long currentTick = serverLevel.getGameTime();
        ResurrectionStoneData.ResurrectionStoneComponent component = getResurrectionStoneData(stack);
        
        // Check cooldown
        if (currentTick - component.lastUseTick() < COOLDOWN_TICKS) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.resurrection_stone.cooldown"));
            return InteractionResult.FAIL;
        }
        
        // Find nearby dead entities (players or mobs that died recently)
        Vec3 playerPos = player.position();
        List<ResurrectionStoneDeathTracker.DeathRecord> recentDeaths = 
            ResurrectionStoneDeathTracker.getRecentDeaths(
                serverLevel, 
                playerPos.x, playerPos.y, playerPos.z, 
                SUMMON_RANGE
            );
        
        if (recentDeaths.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.resurrection_stone.no_deaths"));
            // Update last use even if no deaths found
            component = component.withLastUse(currentTick);
            stack.set(ResurrectionStoneData.RESURRECTION_STONE_DATA.get(), component);
            return InteractionResult.SUCCESS;
        }
        
        // Summon shades for recent deaths
        int shadesSummoned = 0;
        for (ResurrectionStoneDeathTracker.DeathRecord death : recentDeaths) {
            // Add shade to component
            component = component.addShade(death.entityId(), death.entityName(), currentTick);
            shadesSummoned++;
            
            // Spawn visual effect at death location
            serverLevel.sendParticles(ParticleTypes.SOUL,
                death.x(), death.y() + 1.0, death.z(),
                30, 0.5, 0.5, 0.5, 0.1);
            
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                death.x(), death.y() + 1.0, death.z(),
                20, 0.3, 0.3, 0.3, 0.05);
        }
        
        serverPlayer.sendSystemMessage(Component.translatable(
            "message.spells_n_squares.resurrection_stone.shades_summoned", shadesSummoned));
        
        // Visual and audio feedback
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        serverLevel.sendParticles(ParticleTypes.SOUL,
            pos.x, pos.y, pos.z,
            50, 1.0, 1.0, 1.0, 0.1);
        
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            pos.x, pos.y, pos.z,
            30, 0.8, 0.8, 0.8, 0.05);
        
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 0.8f);
        
        // Update component with shades and last use
        component = component.withLastUse(currentTick);
        stack.set(ResurrectionStoneData.RESURRECTION_STONE_DATA.get(), component);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets the Resurrection Stone data component from an item stack.
     */
    public static ResurrectionStoneData.ResurrectionStoneComponent getResurrectionStoneData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ResurrectionStoneItem)) {
            return new ResurrectionStoneData.ResurrectionStoneComponent();
        }
        
        ResurrectionStoneData.ResurrectionStoneComponent data = stack.get(ResurrectionStoneData.RESURRECTION_STONE_DATA.get());
        if (data == null) {
            data = new ResurrectionStoneData.ResurrectionStoneComponent();
            stack.set(ResurrectionStoneData.RESURRECTION_STONE_DATA.get(), data);
        }
        return data;
    }
}
