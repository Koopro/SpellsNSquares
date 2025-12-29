package at.koopro.spells_n_squares.features.creatures.mount;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
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

/**
 * Graphorn - a large aggressive beast from Fantastic Beasts, mountable.
 * Very powerful and tough.
 */
public class GraphornEntity extends BaseTamableCreatureEntity {
    
    public GraphornEntity(EntityType<? extends GraphornEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 120.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.ATTACK_DAMAGE, 12.0D)
            .add(Attributes.ARMOR, 8.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Allow mounting if owned
        if (hasOwner() && !this.isVehicle() && !player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
        
        // Try to tame with rare meat
        if (!hasOwner() && !stack.isEmpty()) {
            if (stack.is(net.minecraft.world.item.Items.COOKED_BEEF) || 
                stack.is(net.minecraft.world.item.Items.COOKED_PORKCHOP) ||
                stack.is(net.minecraft.world.item.Items.COOKED_MUTTON)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    // Graphorns are difficult to tame - need multiple attempts
                    if (this.random.nextFloat() < 0.3f) { // 30% chance per attempt
                        this.setOwner(player);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 0.8f);
                        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.graphorn.tamed"));
                        
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    } else {
                        // Aggressive response
                        this.setTarget(player);
                        this.level().playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.NEUTRAL, 1.0f, 0.5f);
                        serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.graphorn.aggressive"));
                        
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        
        return super.mobInteract(player, hand);
    }
    
    // Note: hurt() is final in Entity, so we can't override it
    // Graphorns' toughness is handled via attributes (ARMOR, KNOCKBACK_RESISTANCE)
    
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.COOKED_BEEF) || 
               stack.is(net.minecraft.world.item.Items.COOKED_PORKCHOP) ||
               stack.is(net.minecraft.world.item.Items.COOKED_MUTTON);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Graphorns don't breed
    }
}
















