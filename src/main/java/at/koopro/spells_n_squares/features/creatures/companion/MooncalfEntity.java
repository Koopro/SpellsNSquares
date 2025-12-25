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
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Mooncalf - a shy creature from Fantastic Beasts that appears during full moon.
 * Very timid but can be tamed.
 */
public class MooncalfEntity extends BaseTamableCreatureEntity {
    
    public MooncalfEntity(EntityType<? extends MooncalfEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame with vegetables
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(net.minecraft.world.item.Items.CARROT) || 
                stack.is(net.minecraft.world.item.Items.POTATO) ||
                stack.is(net.minecraft.world.item.Items.BEETROOT)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    // Mooncalves are shy - need to approach slowly
                    if (this.random.nextFloat() < 0.4f) { // 40% chance
                        this.setOwner(player);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 0.5f, 1.5f);
                        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.mooncalf.tamed"));
                        
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    } else {
                        // Shy response - runs away
                        this.setTarget(null);
                        // Clear attack goals
                        this.goalSelector.removeAllGoals(g -> true);
                        this.registerGoals(); // Re-register goals
                        this.level().playSound(null, this.blockPosition(), SoundEvents.RABBIT_HURT, SoundSource.NEUTRAL, 0.5f, 1.2f);
                        
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
        
        // Mooncalves are more active during full moon (night time)
        if (this.level() instanceof ServerLevel serverLevel) {
            long dayTime = serverLevel.getDayTime() % 24000;
            boolean isNight = dayTime >= 13000 && dayTime <= 23000;
            
            // Visual effect during night
            if (isNight && this.tickCount % 40 == 0 && this.random.nextFloat() < 0.3f) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    3, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.CARROT) || 
               stack.is(net.minecraft.world.item.Items.POTATO) ||
               stack.is(net.minecraft.world.item.Items.BEETROOT);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Mooncalves don't breed
    }
}












