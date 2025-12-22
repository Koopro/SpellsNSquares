package at.koopro.spells_n_squares.features.ghosts;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

/**
 * Base ghost entity class.
 * Ghosts are non-hostile NPCs that can be talked to and provide lore/quests.
 */
public class GhostEntity extends Mob {
    
    private UUID ghostId;
    private GhostData.GhostComponent ghostData;
    
    public GhostEntity(EntityType<? extends GhostEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true; // Ghosts can phase through blocks
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Note: WaterAvoidingRandomStrollGoal requires PathfinderMob, so we skip it for Mob
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }
    
    @Override
    public boolean isPushable() {
        return false; // Ghosts can't be pushed
    }
    
    @Override
    public boolean canCollideWith(net.minecraft.world.entity.Entity entity) {
        return false; // Ghosts phase through entities
    }
    
    /**
     * Sets the ghost data for this entity.
     */
    public void setGhostData(GhostData.GhostComponent data) {
        this.ghostData = data;
        this.ghostId = data.ghostId();
        this.setCustomName(Component.literal(data.name()));
        this.setCustomNameVisible(true);
    }
    
    /**
     * Gets the ghost data.
     */
    public GhostData.GhostComponent getGhostData() {
        return ghostData;
    }
    
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (ghostId != null) {
            output.putString("GhostId", ghostId.toString());
        }
    }
    
    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        String ghostIdStr = input.getStringOr("GhostId", null);
        if (ghostIdStr != null) {
            try {
                this.ghostId = UUID.fromString(ghostIdStr);
                // TODO: Load ghost data from storage
            } catch (IllegalArgumentException e) {
                // Invalid UUID format, ignore
            }
        }
    }
    
    // Note: hurt() is final in Entity, so we can't override it
    // Ghosts will need damage immunity handled differently (e.g., via event handler)
    
    /**
     * Called when a player interacts with the ghost.
     */
    public void interact(Player player) {
        if (player.level().isClientSide() || !(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return;
        }
        
        if (ghostData != null) {
            GhostDialogueSystem.startConversation(serverPlayer, ghostData);
        }
    }
}






