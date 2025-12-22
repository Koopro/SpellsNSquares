package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for generic/shared mod entities that don't belong to a specific feature.
 * Feature-specific entities are registered in their respective feature registries.
 */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SpellsNSquares.MODID);
    
    // Generic entities would be registered here
    // Currently empty as all entities belong to specific features
}
