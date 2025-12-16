package at.koopro.spells_n_squares.features.creatures.companion;

import at.koopro.spells_n_squares.features.creatures.base.BaseTamableCreatureEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

/**
 * Puffskein - a fluffy pet creature from Fantastic Beasts.
 * Provides comfort and regeneration to nearby players.
 */
public class PuffskeinEntity extends BaseTamableCreatureEntity {
    private static final int COMFORT_RANGE = 6;
    private static final int COMFORT_INTERVAL = 100; // Every 5 seconds
    private int comfortTimer = 0;
    
    public PuffskeinEntity(EntityType<? extends PuffskeinEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 15.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Try to tame with food items
        if (!hasOwner() && !stack.isEmpty()) {
            if (this.isFood(stack)) {
                if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    this.setOwner(player);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                    serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.puffskein.tamed"));
                    
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
            comfortTimer++;
            
            // Periodically provide comfort effects to nearby players
            if (comfortTimer >= COMFORT_INTERVAL) {
                comfortTimer = 0;
                provideComfort(serverLevel);
            }
        }
    }
    
    /**
     * Provides regeneration and comfort effects to nearby players.
     */
    private void provideComfort(ServerLevel level) {
        for (Player player : this.level().getEntitiesOfClass(Player.class,
                this.getBoundingBox().inflate(COMFORT_RANGE))) {
            
            // Give regeneration effect
            player.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                100,
                0,
                false,
                true,
                true
            ));
            
            // Visual effect
            level.sendParticles(ParticleTypes.HEART,
                player.getX(), player.getY() + 1.0, player.getZ(),
                3, 0.3, 0.3, 0.3, 0.02);
        }
        
        // Visual effect on Puffskein
        level.sendParticles(ParticleTypes.HEART,
            this.getX(), this.getY() + 0.5, this.getZ(),
            2, 0.2, 0.2, 0.2, 0.01);
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        // Check if item is edible - accept common food items
        if (stack.isEmpty()) {
            return false;
        }
        // Check for common food items
        return stack.is(net.minecraft.world.item.Items.BREAD) ||
               stack.is(net.minecraft.world.item.Items.APPLE) ||
               stack.is(net.minecraft.world.item.Items.COOKED_BEEF) ||
               stack.is(net.minecraft.world.item.Items.COOKED_PORKCHOP) ||
               stack.is(net.minecraft.world.item.Items.COOKED_CHICKEN) ||
               stack.is(net.minecraft.world.item.Items.CARROT) ||
               stack.is(net.minecraft.world.item.Items.POTATO) ||
               stack.is(net.minecraft.world.item.Items.BAKED_POTATO);
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // Puffskeins don't breed
    }
}




