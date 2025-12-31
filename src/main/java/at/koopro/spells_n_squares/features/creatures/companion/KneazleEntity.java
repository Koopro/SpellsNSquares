package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * Kneazle - an intelligent cat-like creature that can detect untrustworthy people.
 */
public class KneazleEntity extends BaseTamableCreatureEntity {
    private static final int DETECTION_RANGE = 16;
    private static final int DETECTION_INTERVAL = 100; // Every 5 seconds
    private int detectionTimer = 0;
    
    public KneazleEntity(EntityType<? extends KneazleEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 25.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame with fish
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.kneazle.tamed"));
                    
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
            detectionTimer++;
            
            // Periodically detect untrustworthy entities
            if (detectionTimer >= DETECTION_INTERVAL) {
                detectionTimer = 0;
                detectUntrustworthy(serverLevel);
            }
        }
    }
    
    /**
     * Detects untrustworthy entities (hostile mobs, players with negative reputation, etc.).
     */
    private void detectUntrustworthy(ServerLevel level) {
        AABB searchArea = new AABB(this.blockPosition()).inflate(DETECTION_RANGE);
        var entities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
            entity -> entity != this && entity.isAlive());
        
        for (LivingEntity entity : entities) {
            // Detect hostile mobs
            if (entity instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() != null) {
                // Visual warning
                level.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ(),
                    5, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Kneazles don't breed
    }
}

















