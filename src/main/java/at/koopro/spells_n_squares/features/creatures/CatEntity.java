package at.koopro.spells_n_squares.features.creatures;

import at.koopro.spells_n_squares.features.creatures.util.CreatureOwnerHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * Cat entity - a familiar companion creature.
 */
public class CatEntity extends TamableAnimal {
    private Optional<UUID> ownerId = Optional.empty();
    private int loyalty = 0;
    
    public CatEntity(EntityType<? extends CatEntity> type, Level level) {
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
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
    
    /**
     * Sets the owner of this cat.
     */
    public void setOwner(Player player) {
        this.ownerId = Optional.of(player.getUUID());
        this.loyalty = 50; // Initial loyalty
    }
    
    /**
     * Gets the owner UUID.
     */
    public Optional<UUID> getOwnerId() {
        return ownerId;
    }
    
    /**
     * Increases loyalty when fed or cared for.
     */
    public void increaseLoyalty(int amount) {
        this.loyalty = Math.min(100, this.loyalty + amount);
    }
    
    /**
     * Gets the current loyalty level.
     */
    public int getLoyalty() {
        return loyalty;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Custom cats are not breedable/food-driven by default
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        // This cat does not currently support breeding
        return null;
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        CreatureOwnerHelper.saveOwner(output, ownerId);
        output.putInt("Loyalty", loyalty);
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        ownerId = CreatureOwnerHelper.loadOwner(input);
        loyalty = input.getIntOr("Loyalty", 0);
    }
}


















