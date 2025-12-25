package at.koopro.spells_n_squares.features.creatures.util;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;

/**
 * Utility class for registering common AI goals for creature entities.
 * Provides standardized goal registration patterns to reduce duplication.
 */
public final class CreatureGoalHelper {
    private CreatureGoalHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Registers basic goals that most tamable creatures need.
     * Includes: Float, SitWhenOrderedTo, FollowOwner, WaterAvoidingRandomStroll, LookAtPlayer, RandomLookAround
     * 
     * @param entity The entity to register goals for
     * @param followSpeed Speed for FollowOwnerGoal (typically 1.0-1.5)
     * @param followMinDistance Minimum distance for FollowOwnerGoal (typically 10.0)
     * @param followMaxDistance Maximum distance for FollowOwnerGoal (typically 2.0)
     * @param strollSpeed Speed for WaterAvoidingRandomStrollGoal (typically 1.0)
     */
    public static void registerBasicGoals(TamableAnimal entity, double followSpeed, float followMinDistance, float followMaxDistance, double strollSpeed) {
        entity.goalSelector.addGoal(0, new FloatGoal(entity));
        entity.goalSelector.addGoal(1, new SitWhenOrderedToGoal(entity));
        entity.goalSelector.addGoal(2, new FollowOwnerGoal(entity, followSpeed, followMinDistance, followMaxDistance));
        entity.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(entity, strollSpeed));
        entity.goalSelector.addGoal(4, new LookAtPlayerGoal(entity, Player.class, 8.0F));
        entity.goalSelector.addGoal(5, new RandomLookAroundGoal(entity));
    }
    
    /**
     * Registers companion creature goals (includes PanicGoal).
     * Includes: Float, Panic, SitWhenOrderedTo, FollowOwner, WaterAvoidingRandomStroll, LookAtPlayer, RandomLookAround
     * 
     * @param entity The entity to register goals for
     * @param panicSpeed Speed for PanicGoal (typically 1.5-2.0)
     * @param followSpeed Speed for FollowOwnerGoal (typically 1.0)
     * @param followMinDistance Minimum distance for FollowOwnerGoal (typically 10.0)
     * @param followMaxDistance Maximum distance for FollowOwnerGoal (typically 2.0)
     * @param strollSpeed Speed for WaterAvoidingRandomStrollGoal (typically 1.0)
     */
    public static void registerCompanionGoals(TamableAnimal entity, double panicSpeed, double followSpeed, float followMinDistance, float followMaxDistance, double strollSpeed) {
        entity.goalSelector.addGoal(0, new FloatGoal(entity));
        entity.goalSelector.addGoal(1, new PanicGoal(entity, panicSpeed));
        entity.goalSelector.addGoal(2, new SitWhenOrderedToGoal(entity));
        entity.goalSelector.addGoal(3, new FollowOwnerGoal(entity, followSpeed, followMinDistance, followMaxDistance));
        entity.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(entity, strollSpeed));
        entity.goalSelector.addGoal(5, new LookAtPlayerGoal(entity, Player.class, 8.0F));
        entity.goalSelector.addGoal(6, new RandomLookAroundGoal(entity));
    }
    
    /**
     * Registers mount creature goals (no PanicGoal, optimized for mounts).
     * Includes: Float, SitWhenOrderedTo, FollowOwner, WaterAvoidingRandomStroll, LookAtPlayer, RandomLookAround
     * 
     * @param entity The entity to register goals for
     * @param followSpeed Speed for FollowOwnerGoal (typically 1.5)
     * @param followMinDistance Minimum distance for FollowOwnerGoal (typically 10.0)
     * @param followMaxDistance Maximum distance for FollowOwnerGoal (typically 2.0)
     * @param strollSpeed Speed for WaterAvoidingRandomStrollGoal (typically 1.0-1.2)
     */
    public static void registerMountGoals(TamableAnimal entity, double followSpeed, float followMinDistance, float followMaxDistance, double strollSpeed) {
        entity.goalSelector.addGoal(0, new FloatGoal(entity));
        entity.goalSelector.addGoal(1, new SitWhenOrderedToGoal(entity));
        entity.goalSelector.addGoal(2, new FollowOwnerGoal(entity, followSpeed, followMinDistance, followMaxDistance));
        entity.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(entity, strollSpeed));
        entity.goalSelector.addGoal(4, new LookAtPlayerGoal(entity, Player.class, 8.0F));
        entity.goalSelector.addGoal(5, new RandomLookAroundGoal(entity));
    }
    
    /**
     * Registers aggressive creature goals (includes MeleeAttackGoal).
     * Includes: Float, SitWhenOrderedTo, MeleeAttack, FollowOwner, WaterAvoidingRandomStroll, LookAtPlayer, RandomLookAround
     * 
     * @param entity The entity to register goals for
     * @param attackSpeed Speed for MeleeAttackGoal (typically 1.0-1.2)
     * @param followSpeed Speed for FollowOwnerGoal (typically 1.0)
     * @param followMinDistance Minimum distance for FollowOwnerGoal (typically 10.0)
     * @param followMaxDistance Maximum distance for FollowOwnerGoal (typically 2.0)
     * @param strollSpeed Speed for WaterAvoidingRandomStrollGoal (typically 1.0)
     */
    public static void registerAggressiveGoals(TamableAnimal entity, double attackSpeed, double followSpeed, float followMinDistance, float followMaxDistance, double strollSpeed) {
        entity.goalSelector.addGoal(0, new FloatGoal(entity));
        entity.goalSelector.addGoal(1, new SitWhenOrderedToGoal(entity));
        entity.goalSelector.addGoal(2, new MeleeAttackGoal(entity, attackSpeed, true));
        entity.goalSelector.addGoal(3, new FollowOwnerGoal(entity, followSpeed, followMinDistance, followMaxDistance));
        entity.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(entity, strollSpeed));
        entity.goalSelector.addGoal(5, new LookAtPlayerGoal(entity, Player.class, 8.0F));
        entity.goalSelector.addGoal(6, new RandomLookAroundGoal(entity));
    }
}











