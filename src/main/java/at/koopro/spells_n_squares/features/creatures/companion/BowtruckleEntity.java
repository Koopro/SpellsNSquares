package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bowtruckle - a small tree guardian creature from Fantastic Beasts.
 * Helps with plant growth and tree protection.
 */
public class BowtruckleEntity extends BaseTamableCreatureEntity {
    private static final int PLANT_GROWTH_RADIUS = 8;
    private static final int PLANT_GROWTH_INTERVAL = 200; // Every 10 seconds
    private int plantGrowthTimer = 0;
    
    public BowtruckleEntity(EntityType<? extends BowtruckleEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame with saplings or plant-related items
        if (!hasOwner() && !stack.isEmpty()) {
            if (isPlantItem(stack)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.bowtruckle.tamed"));
                    
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        
        // If already tamed, sit/stand
        if (this.isTame() && isOwner(player)) {
            if (!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.SUCCESS;
        }
        
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (this.level() instanceof ServerLevel serverLevel && this.isTame() && hasOwner() && !this.isOrderedToSit()) {
            plantGrowthTimer++;
            
            // Periodically help plants grow
            if (plantGrowthTimer >= PLANT_GROWTH_INTERVAL) {
                plantGrowthTimer = 0;
                helpPlantGrowth(serverLevel);
            }
        }
    }
    
    /**
     * Helps nearby crops and saplings grow faster.
     */
    private void helpPlantGrowth(ServerLevel level) {
        BlockPos pos = this.blockPosition();
        
        for (int x = -PLANT_GROWTH_RADIUS; x <= PLANT_GROWTH_RADIUS; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -PLANT_GROWTH_RADIUS; z <= PLANT_GROWTH_RADIUS; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();
                    
                    // Help crops grow
                    if (block == Blocks.WHEAT || block == Blocks.CARROTS || 
                        block == Blocks.POTATOES || block == Blocks.BEETROOTS ||
                        block == Blocks.MELON_STEM || block == Blocks.PUMPKIN_STEM) {
                        
                        // Random tick to help growth
                        if (level.random.nextFloat() < 0.3f) {
                            state.randomTick(level, checkPos, level.random);
                            
                            // Visual effect
                            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5,
                                2, 0.2, 0.2, 0.2, 0.01);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Checks if an item is plant-related and can be used for taming.
     */
    private boolean isPlantItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        return stack.is(Items.OAK_SAPLING) || stack.is(Items.BIRCH_SAPLING) ||
               stack.is(Items.SPRUCE_SAPLING) || stack.is(Items.JUNGLE_SAPLING) ||
               stack.is(Items.ACACIA_SAPLING) || stack.is(Items.DARK_OAK_SAPLING) ||
               stack.is(Items.CHERRY_SAPLING) || stack.is(Items.MANGROVE_PROPAGULE) ||
               stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS) ||
               stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS);
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return isPlantItem(stack);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Bowtruckles don't breed
    }
}
















