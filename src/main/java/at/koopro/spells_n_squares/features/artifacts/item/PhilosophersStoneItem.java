package at.koopro.spells_n_squares.features.artifacts.item;

import at.koopro.spells_n_squares.features.artifacts.data.PhilosophersStoneData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Philosopher's Stone artifact that provides regeneration and item transmutation.
 */
public class PhilosophersStoneItem extends Item {
    
    private static final int COOLDOWN_TICKS = 1200; // 60 seconds - very long cooldown
    private static final int REGENERATION_DURATION = 400; // 20 seconds
    
    public PhilosophersStoneItem(Properties properties) {
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
        PhilosophersStoneData.PhilosophersStoneComponent component = getPhilosophersStoneData(stack);
        
        // Check cooldown
        if (currentTick - component.lastUseTick() < COOLDOWN_TICKS) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.philosophers_stone.cooldown"));
            return InteractionResult.FAIL;
        }
        
        // Check uses remaining
        if (!component.canUse()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.philosophers_stone.exhausted"));
            return InteractionResult.FAIL;
        }
        
        // Apply regeneration effect
        player.addEffect(new MobEffectInstance(
            MobEffects.REGENERATION,
            REGENERATION_DURATION,
            2, // Level 3 regeneration
            false,
            true,
            true
        ));
        
        // Try to transmute items in inventory (iron to gold, etc.)
        boolean transmuted = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (!invStack.isEmpty()) {
                // Simple transmutation: iron ingot to gold ingot
                if (invStack.is(Items.IRON_INGOT) && invStack.getCount() >= 1) {
                    invStack.shrink(1);
                    ItemStack goldStack = new ItemStack(Items.GOLD_INGOT, 1);
                    if (player.getInventory().add(goldStack)) {
                        transmuted = true;
                    }
                }
            }
        }
        
        if (transmuted) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.philosophers_stone.transmuted"));
        }
        
        // Update component
        component = component.withUse(currentTick);
        stack.set(PhilosophersStoneData.PHILOSOPHERS_STONE_DATA.get(), component);
        
        // Visual and audio feedback
        Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
        serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
            pos.x, pos.y, pos.z,
            50, 0.8, 0.8, 0.8, 0.1);
        
        serverLevel.sendParticles(ParticleTypes.ENCHANT,
            pos.x, pos.y, pos.z,
            30, 0.5, 0.5, 0.5, 0.05);
        
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.5f);
        
        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.philosophers_stone.activated"));
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets the Philosopher's Stone data component from an item stack.
     */
    public static PhilosophersStoneData.PhilosophersStoneComponent getPhilosophersStoneData(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof PhilosophersStoneItem)) {
            return new PhilosophersStoneData.PhilosophersStoneComponent();
        }
        
        PhilosophersStoneData.PhilosophersStoneComponent data = stack.get(PhilosophersStoneData.PHILOSOPHERS_STONE_DATA.get());
        if (data == null) {
            data = new PhilosophersStoneData.PhilosophersStoneComponent();
            stack.set(PhilosophersStoneData.PHILOSOPHERS_STONE_DATA.get(), data);
        }
        return data;
    }
}
