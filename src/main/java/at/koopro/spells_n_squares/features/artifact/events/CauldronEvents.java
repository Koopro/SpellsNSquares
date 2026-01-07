package at.koopro.spells_n_squares.features.artifact.events;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.event.SafeEventHandler;
import at.koopro.spells_n_squares.features.artifact.ArtifactRegistry;
import at.koopro.spells_n_squares.features.artifact.block.ElixirCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.List;

/**
 * Handles cauldron interactions for Elixir creation.
 * - Gold Block + Magma Cream in water cauldron -> Elixir Base
 * - Philosopher's Stone + Elixir Base -> Elixir of Life
 */
@EventBusSubscriber(modid = SpellsNSquares.MODID)
public class CauldronEvents {
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        
        // Check every 10 ticks for performance
        if (level.getGameTime() % 10 != 0) {
            return;
        }
        
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        
        SafeEventHandler.execute(() -> {
            // Find item entities that might be near cauldrons (Gold Block or Magma Cream)
            // Use a reasonable search area - check around loaded chunks
            AABB worldBounds = new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000);
            List<ItemEntity> allItems = serverLevel.getEntitiesOfClass(ItemEntity.class, worldBounds);
            
            for (ItemEntity itemEntity : allItems) {
                ItemStack stack = itemEntity.getItem();
                // Only process Gold Block or Magma Cream
                if (!stack.is(Items.GOLD_BLOCK) && !stack.is(Items.MAGMA_CREAM)) {
                    continue;
                }
                BlockPos itemPos = itemEntity.blockPosition();
                
                // Check cauldron below the item
                BlockPos cauldronPos = itemPos.below();
                BlockState state = level.getBlockState(cauldronPos);
                
                if (state.is(Blocks.WATER_CAULDRON)) {
                    checkWaterCauldronForElixirBase(serverLevel, cauldronPos, state);
                }
            }
        }, "checking cauldron events");
    }
    
    /**
     * Checks if a water cauldron has Gold Block and Magma Cream to create Elixir Base.
     */
    private static void checkWaterCauldronForElixirBase(ServerLevel level, BlockPos pos, BlockState state) {
        // Check if cauldron is full
        int levelValue = state.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL);
        if (levelValue != 3) {
            return;
        }
        
        // Check for items in cauldron area
        AABB searchBox = new AABB(pos).inflate(0.5);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchBox);
        
        ItemEntity goldBlockEntity = null;
        ItemEntity magmaCreamEntity = null;
        
        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            if (stack.is(Items.GOLD_BLOCK) && goldBlockEntity == null) {
                goldBlockEntity = itemEntity;
            } else if (stack.is(Items.MAGMA_CREAM) && magmaCreamEntity == null) {
                magmaCreamEntity = itemEntity;
            }
        }
        
        if (goldBlockEntity != null && magmaCreamEntity != null) {
            // Consume ingredients
            goldBlockEntity.getItem().shrink(1);
            if (goldBlockEntity.getItem().isEmpty()) {
                goldBlockEntity.discard();
            }
            
            magmaCreamEntity.getItem().shrink(1);
            if (magmaCreamEntity.getItem().isEmpty()) {
                magmaCreamEntity.discard();
            }
            
            // Convert water cauldron to Elixir Base cauldron (full level)
            level.setBlock(pos, ArtifactRegistry.ELIXIR_BASE_CAULDRON.value()
                .defaultBlockState()
                .setValue(ElixirCauldronBlock.LEVEL, 3), 3);
            
            // Spawn result item above cauldron (elixir base bucket)
            if (level instanceof ServerLevel serverLevel) {
                BlockPos spawnPos = pos.above();
                ItemStack resultItem = new ItemStack(
                    at.koopro.spells_n_squares.features.artifact.fluid.ElixirFluids.ELIXIR_BASE_BUCKET.get(), 1);
                ItemEntity resultEntity = new ItemEntity(serverLevel,
                    spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5,
                    resultItem);
                resultEntity.setPickUpDelay(20); // 1 second pickup delay
                resultEntity.setDeltaMovement(0, 0.2, 0); // Slight upward motion
                serverLevel.addFreshEntity(resultEntity);
            }
            
            // Visual effects
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.BUBBLE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                20, 0.3, 0.1, 0.3, 0.1);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.2, 0.1, 0.2, 0.05);
            
            level.playSound(null, pos,
                net.minecraft.sounds.SoundEvents.BREWING_STAND_BREW,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 0.9f);
        }
    }
}

