package at.koopro.spells_n_squares.features.artifact;

import at.koopro.spells_n_squares.core.base.item.BaseGeoItem;
import at.koopro.spells_n_squares.core.data.ItemDataHelper;
import at.koopro.spells_n_squares.core.fx.ParticlePool;
import at.koopro.spells_n_squares.core.util.rendering.ColorUtils;
import at.koopro.spells_n_squares.features.artifact.client.PhilosophersStoneItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * The Philosopher's Stone - allows transmutation of matter and creation of the Elixir of Life.
 * Every transmutation generates Entropy, which can cause catastrophic backfire at 100%.
 */
public class PhilosophersStoneItem extends BaseGeoItem {
    
    private static final int ENTROPY_PER_TRANSMUTATION = 5;
    private static final int MATERIA_PER_ITEM = 100;
    private static final int BACKFIRE_EXPLOSION_RADIUS = 3;
    
    public PhilosophersStoneItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        // Shift-right-click: Dissolution (consume offhand item)
        if (player.isShiftKeyDown()) {
            return handleDissolution(level, player, hand);
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        
        // 1. ELIXIR CREATION (Elixir Base Cauldron -> Elixir of Life Cauldron)
        if (state.getBlock() == ArtifactRegistry.ELIXIR_BASE_CAULDRON.get()) {
            int levelValue = state.getValue(at.koopro.spells_n_squares.features.artifact.block.ElixirCauldronBlock.LEVEL);
            if (levelValue == 3) {
                return handleElixirBaseToLife(level, pos, player, stack);
            }
        }
        
        // 2. TRANSMUTATION (World Block)
        return handleBlockTransmutation(level, pos, state, player, stack);
    }
    
    /**
     * Handles dissolution: consumes offhand item and adds materia.
     */
    private InteractionResult handleDissolution(Level level, Player player, InteractionHand hand) {
        ItemStack offhand = player.getOffhandItem();
        if (offhand.isEmpty()) {
            if (player instanceof ServerPlayer serverPlayer) {
                Component baseMessage = Component.translatable("message.spells_n_squares.philosophers_stone.no_offhand");
                Component coloredMessage = ColorUtils.coloredText(baseMessage.getString(), ColorUtils.SPELL_RED);
                serverPlayer.sendSystemMessage(coloredMessage);
            }
            return InteractionResult.FAIL;
        }
        
        ItemStack stone = player.getItemInHand(hand);
        PhilosophersStoneData.StoneComponent data = getStoneData(stone);
        
        // Add materia
        data = data.addMateria(MATERIA_PER_ITEM);
        setStoneData(stone, data);
        
        // Consume offhand item
        if (!player.getAbilities().instabuild) {
            offhand.shrink(1);
        }
        
        // Visual effects
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = new Vec3(player.getX(), player.getY() + 1.0, player.getZ());
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.ENCHANT,
                pos,
                20, 0.5, 0.5, 0.5, 0.1
            );
            level.playSound(null, player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.5f);
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            Component message = Component.translatable("message.spells_n_squares.philosophers_stone.dissolved",
                offhand.getDisplayName());
            serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_GREEN));
        }
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles conversion of Elixir Base cauldron to Elixir of Life cauldron.
     */
    private InteractionResult handleElixirBaseToLife(Level level, BlockPos pos, Player player, ItemStack stack) {
        // Convert Elixir Base cauldron to Elixir of Life cauldron (full level)
        level.setBlock(pos, ArtifactRegistry.ELIXIR_OF_LIFE_CAULDRON.value()
            .defaultBlockState()
            .setValue(at.koopro.spells_n_squares.features.artifact.block.ElixirCauldronBlock.LEVEL, 3), 3);
        
        // Spawn result item above cauldron
        if (level instanceof ServerLevel serverLevel) {
            BlockPos spawnPos = pos.above();
            ItemStack resultItem = new ItemStack(ArtifactRegistry.ELIXIR_OF_LIFE.get(), 1);
            net.minecraft.world.entity.item.ItemEntity resultEntity = new net.minecraft.world.entity.item.ItemEntity(serverLevel,
                spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5,
                resultItem);
            resultEntity.setPickUpDelay(20); // 1 second pickup delay
            resultEntity.setDeltaMovement(0, 0.2, 0); // Slight upward motion
            serverLevel.addFreshEntity(resultEntity);
        }
        
        // Visual effects
        if (level instanceof ServerLevel serverLevel) {
            Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.FLAME,
                center,
                30, 0.3, 0.3, 0.3, 0.1
            );
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.ENCHANT,
                center,
                20, 0.3, 0.3, 0.3, 0.1
            );
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.END_ROD,
                center,
                15, 0.2, 0.2, 0.2, 0.05
            );
            level.playSound(null, pos,
                SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
        }
        
        if (player instanceof ServerPlayer serverPlayer) {
            Component message = Component.translatable("message.spells_n_squares.philosophers_stone.elixir_life_created");
            serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_GREEN));
        }
        
        // Consume 1 Philosopher's Stone
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Handles block transmutation: transforms blocks into learned materials.
     */
    private InteractionResult handleBlockTransmutation(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        PhilosophersStoneData.StoneComponent data = getStoneData(stack);
        
        // Check for backfire first
        if (data.isAtCriticalMass()) {
            triggerBackfire(level, pos, player, stack);
            return InteractionResult.FAIL;
        }
        
        // Check materia
        if (data.materia() < MATERIA_PER_ITEM) {
            if (player instanceof ServerPlayer serverPlayer) {
                Component message = Component.translatable("message.spells_n_squares.philosophers_stone.insufficient_materia");
                serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_RED));
            }
            return InteractionResult.FAIL;
        }
        
        // Get transmutation target
        BlockState target = getTransmutationTarget(state);
        if (target == null) {
            if (player instanceof ServerPlayer serverPlayer) {
                Component message = Component.translatable("message.spells_n_squares.philosophers_stone.cannot_transmute");
                serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_RED));
            }
            return InteractionResult.FAIL;
        }
        
        // Perform transmutation
        level.setBlock(pos, target, 3);
        
        // Update stone data: consume materia, add entropy
        data = data.addMateria(-MATERIA_PER_ITEM).addEntropy(ENTROPY_PER_TRANSMUTATION);
        setStoneData(stack, data);
        
        // Visual effects based on entropy level
        if (level instanceof ServerLevel serverLevel) {
            Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (data.isInDangerZone()) {
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.SMOKE,
                    center,
                    10, 0.3, 0.3, 0.3, 0.05
                );
            } else {
                ParticlePool.queueParticle(
                    serverLevel,
                    ParticleTypes.ENCHANT,
                    center,
                    15, 0.3, 0.3, 0.3, 0.1
                );
            }
            level.playSound(null, pos,
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);
        }
        
        // Check for backfire after adding entropy
        if (player instanceof ServerPlayer serverPlayer) {
            if (data.isAtCriticalMass()) {
                Component message = Component.translatable("message.spells_n_squares.philosophers_stone.critical_mass_warning");
                serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_RED));
            } else if (data.isInDangerZone()) {
                Component message = Component.translatable("message.spells_n_squares.philosophers_stone.danger_zone_warning",
                    data.entropy());
                serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_GOLD));
            }
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Gets the transmutation target for a given block state.
     * Simple hardcoded pairs for now.
     */
    private BlockState getTransmutationTarget(BlockState state) {
        // Iron -> Gold
        if (state.is(Blocks.IRON_BLOCK)) {
            return Blocks.GOLD_BLOCK.defaultBlockState();
        }
        // Lead (if exists) -> Gold, or use Iron Ore -> Gold Ore
        if (state.is(Blocks.IRON_ORE)) {
            return Blocks.GOLD_ORE.defaultBlockState();
        }
        // Copper -> Gold
        if (state.is(Blocks.COPPER_BLOCK)) {
            return Blocks.GOLD_BLOCK.defaultBlockState();
        }
        // Stone -> Gold (expensive but possible)
        if (state.is(Blocks.STONE)) {
            return Blocks.GOLD_BLOCK.defaultBlockState();
        }
        return null;
    }
    
    /**
     * Triggers entropy backfire: explosion, effects, inventory corruption.
     */
    private void triggerBackfire(Level level, BlockPos pos, Player player, ItemStack stack) {
        // Explosion
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                BACKFIRE_EXPLOSION_RADIUS, Level.ExplosionInteraction.BLOCK);
        }
        
        // Apply "Heavy Metal Poisoning"
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.SLOWNESS, 40, 3, false, false));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.POISON, 40, 1, false, false));
        
        // Random inventory corruption: 1-3 items -> Cobblestone
        RandomSource random = level.getRandom();
        int corruptionCount = random.nextInt(3) + 1;
        for (int i = 0; i < corruptionCount && i < player.getInventory().getContainerSize(); i++) {
            int slot = random.nextInt(player.getInventory().getContainerSize());
            ItemStack corrupted = player.getInventory().getItem(slot);
            if (!corrupted.isEmpty() && !corrupted.is(stack.getItem())) {
                player.getInventory().setItem(slot, new ItemStack(Blocks.COBBLESTONE, corrupted.getCount()));
            }
        }
        
        // Reset entropy
        PhilosophersStoneData.StoneComponent data = getStoneData(stack);
        data = data.withEntropy(0);
        setStoneData(stack, data);
        
        // Visual effects
        if (level instanceof ServerLevel serverLevel) {
            Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.EXPLOSION,
                center,
                10, 0.5, 0.5, 0.5, 0.1
            );
            ParticlePool.queueParticle(
                serverLevel,
                ParticleTypes.SMOKE,
                new Vec3(player.getX(), player.getY() + 1.0, player.getZ()),
                50, 0.5, 0.5, 0.5, 0.1
            );
        }
        
        level.playSound(null, pos,
            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        
        if (player instanceof ServerPlayer serverPlayer) {
            Component message = Component.translatable("message.spells_n_squares.philosophers_stone.backfire");
            serverPlayer.sendSystemMessage(ColorUtils.coloredText(message.getString(), ColorUtils.SPELL_RED));
        }
    }
    
    /**
     * Gets stone data from item stack.
     */
    private PhilosophersStoneData.StoneComponent getStoneData(ItemStack stack) {
        return ItemDataHelper.getData(stack, PhilosophersStoneData.STONE_DATA.get())
            .orElse(PhilosophersStoneData.StoneComponent.createDefault());
    }
    
    /**
     * Sets stone data on item stack.
     */
    private void setStoneData(ItemStack stack, PhilosophersStoneData.StoneComponent data) {
        ItemDataHelper.setData(stack, PhilosophersStoneData.STONE_DATA.get(), data);
    }
    
    @Override
    protected GeoItemRenderer<?> createRenderer() {
        return new PhilosophersStoneItemRenderer();
    }
}

