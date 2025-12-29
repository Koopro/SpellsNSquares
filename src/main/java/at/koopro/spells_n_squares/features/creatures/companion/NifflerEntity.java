package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import at.koopro.spells_n_squares.features.creatures.companion.ai.NifflerFindTreasureGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.AABB;

import java.util.UUID;

/**
 * Niffler - a treasure-hunting creature from Fantastic Beasts.
 * Finds valuable items and brings them to its owner.
 */
public class NifflerEntity extends BaseTamableCreatureEntity {
    private static final int TREASURE_SEARCH_RADIUS = 16;
    private static final int TREASURE_SEARCH_INTERVAL = 100; // Every 5 seconds
    private int treasureSearchTimer = 0;
    private ItemEntity targetItem = null;
    
    public NifflerEntity(EntityType<? extends NifflerEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new NifflerFindTreasureGoal(this));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame with gold or valuable items
        if (!hasOwner() && !stack.isEmpty()) {
            if (isValuableItem(stack)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.niffler.tamed"));
                    
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
        
        if (this.level() instanceof ServerLevel serverLevel && this.isTame() && hasOwner()) {
            treasureSearchTimer++;
            
            // Periodically search for treasure
            if (treasureSearchTimer >= TREASURE_SEARCH_INTERVAL) {
                treasureSearchTimer = 0;
                searchForTreasure(serverLevel);
            }
        }
    }
    
    /**
     * Searches for valuable items nearby and marks them as targets.
     */
    private void searchForTreasure(ServerLevel level) {
        if (this.isOrderedToSit()) {
            return; // Don't search if sitting
        }
        
        AABB searchArea = new AABB(this.blockPosition()).inflate(TREASURE_SEARCH_RADIUS);
        var items = level.getEntitiesOfClass(ItemEntity.class, searchArea);
        
        ItemEntity bestItem = null;
        double bestValue = 0.0;
        
        for (ItemEntity item : items) {
            if (!item.isAlive() || item.hasPickUpDelay()) {
                continue;
            }
            
            ItemStack stack = item.getItem();
            double value = getItemValue(stack);
            
            if (value > bestValue && value > 0) {
                bestItem = item;
                bestValue = value;
            }
        }
        
        if (bestItem != null) {
            this.targetItem = bestItem;
            // Visual effect
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                bestItem.getX(), bestItem.getY() + 0.5, bestItem.getZ(),
                3, 0.2, 0.2, 0.2, 0.01);
        }
    }
    
    /**
     * Gets the value of an item for treasure hunting purposes.
     */
    private double getItemValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0;
        }
        
        // Gold items are most valuable
        if (stack.is(Items.GOLD_INGOT) || stack.is(Items.GOLD_NUGGET) || stack.is(Items.GOLDEN_APPLE)) {
            return 100.0;
        }
        
        // Other valuable items
        if (stack.is(Items.DIAMOND) || stack.is(Items.EMERALD)) {
            return 50.0;
        }
        
        // Currency items (if mod currency exists)
        // This would check for galleons, sickles, knuts if they exist
        
        // Other metals
        if (stack.is(Items.IRON_INGOT) || stack.is(Items.COPPER_INGOT)) {
            return 10.0;
        }
        
        return 0.0;
    }
    
    /**
     * Checks if an item is valuable enough to use for taming.
     */
    private boolean isValuableItem(ItemStack stack) {
        return getItemValue(stack) >= 10.0;
    }
    
    /**
     * Gets the current target item the Niffler is seeking.
     */
    public ItemEntity getTargetItem() {
        return targetItem;
    }
    
    /**
     * Sets the target item (used by AI goal).
     */
    public void setTargetItem(ItemEntity item) {
        this.targetItem = item;
    }
    
    /**
     * Collects an item and brings it to the owner.
     */
    public boolean collectItem(ItemEntity itemEntity) {
        if (!hasOwner() || !(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        
        UUID ownerUUID = getOwnerId().orElse(null);
        if (ownerUUID == null) {
            return false;
        }
        Player owner = serverLevel.getPlayerByUUID(ownerUUID);
        
        if (owner == null) {
            return false;
        }
        
        ItemStack stack = itemEntity.getItem();
        
        // Try to give item to owner
        if (owner.getInventory().add(stack)) {
            // Success - item added to inventory
            itemEntity.remove(Entity.RemovalReason.DISCARDED);
            
            // Visual and sound effects
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                this.getX(), this.getY() + 0.5, this.getZ(),
                10, 0.3, 0.3, 0.3, 0.05);
            
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.5f, 1.2f);
            
            this.targetItem = null;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return isValuableItem(stack);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Nifflers don't breed
    }
}
















