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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

/**
 * Erumpent - an explosive horned creature from Fantastic Beasts.
 * Dangerous but can be tamed. Has explosive attacks.
 */
public class ErumpentEntity extends BaseTamableCreatureEntity {
    private static final int EXPLOSION_COOLDOWN = 300; // 15 seconds
    private int explosionCooldownTimer = 0;
    
    public ErumpentEntity(EntityType<? extends ErumpentEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 10.0D)
            .add(Attributes.ARMOR, 6.0D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame with rare items
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(net.minecraft.world.item.Items.GOLDEN_APPLE) || 
                stack.is(net.minecraft.world.item.Items.ENCHANTED_GOLDEN_APPLE)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    // Erumpents are very difficult to tame - low chance
                    if (this.random.nextFloat() < 0.2f) { // 20% chance
                        this.setOwner(player);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 0.8f);
                        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.erumpent.tamed"));
                        
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    } else {
                        // Aggressive response - charge attack
                        this.setTarget(player);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.NEUTRAL, 1.0f, 0.3f);
                        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.erumpent.aggressive"));
                        
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
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
        
        if (this.level() instanceof ServerLevel serverLevel) {
            explosionCooldownTimer++;
            
            // When attacking or threatened, create explosive effects
            if (explosionCooldownTimer >= EXPLOSION_COOLDOWN && (this.getTarget() != null || this.random.nextFloat() < 0.05f)) {
                explosionCooldownTimer = 0;
                createExplosiveEffect(serverLevel);
            }
        }
    }
    
    /**
     * Creates an explosive effect around the Erumpent.
     */
    private void createExplosiveEffect(ServerLevel level) {
        AABB area = this.getBoundingBox().inflate(3.0);
        
        // Visual effect
        level.sendParticles(ParticleTypes.EXPLOSION,
            this.getX(), this.getY() + 1.0, this.getZ(),
            5, 1.0, 1.0, 1.0, 0.1);
        
        level.sendParticles(ParticleTypes.SMOKE,
            this.getX(), this.getY() + 1.0, this.getZ(),
            30, 2.0, 1.0, 2.0, 0.1);
        
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 0.8f, 1.2f);
    }
    
    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        // Erumpent attacks can cause explosive damage
        if (hurt && this.random.nextFloat() < 0.3f) {
            createExplosiveEffect(level);
            if (target instanceof net.minecraft.world.entity.LivingEntity living) {
                living.hurt(this.damageSources().mobAttack(this), 5.0f);
            }
        }
        return hurt;
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.GOLDEN_APPLE) || 
               stack.is(net.minecraft.world.item.Items.ENCHANTED_GOLDEN_APPLE);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Erumpents don't breed
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ExplosionCooldown", explosionCooldownTimer);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        explosionCooldownTimer = input.getIntOr("ExplosionCooldown", 0);
    }
}




