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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * House Elf - companion creature that can be tamed by giving clothing.
 */
public class HouseElfEntity extends BaseTamableCreatureEntity {
    private int loyalty = 0;
    private boolean hasClothing = false; // Freed by clothing
    
    public HouseElfEntity(EntityType<? extends HouseElfEntity> type, Level level) {
        super(type, level);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }
    
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Taming: give clothing (any item) to free the house elf
        if (!hasOwner() && !stack.isEmpty()) {
            if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                // Free the house elf by giving clothing
                this.setOwner(player);
                this.hasClothing = true;
                this.loyalty = 100; // Maximum loyalty when freed
                
                // Consume the item
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                
                this.level().playSound(null, this.blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.house_elf.freed", player.getName()));
                
                // Visual effect
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        
        // If already owned, show loyalty status
        if (isOwner(player)) {
            if (!this.level().isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.house_elf.loyalty", this.loyalty));
            }
            return InteractionResult.SUCCESS;
        }
        
        return super.mobInteract(player, hand);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // House elves are helpful - occasionally help nearby players
        if (this.level() instanceof ServerLevel serverLevel && hasOwner() && this.tickCount % 100 == 0) {
            // Small helpful effect (could be expanded)
            if (this.tickCount % 200 == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    3, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
    
    public int getLoyalty() {
        return loyalty;
    }
    
    public boolean hasClothing() {
        return hasClothing;
    }
    
    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
    
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null; // No breeding
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Loyalty", loyalty);
        output.putBoolean("HasClothing", hasClothing);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        loyalty = input.getIntOr("Loyalty", 0);
        hasClothing = input.getBooleanOr("HasClothing", false);
    }
}
