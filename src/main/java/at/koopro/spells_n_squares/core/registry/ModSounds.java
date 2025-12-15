package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all mod sounds.
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, SpellsNSquares.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> RUBBER_DUCK_SQUEAK = SOUNDS.register(
            "rubber_duck_squeak", 
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "rubber_duck_squeak"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> FLASHLIGHT_ON = SOUNDS.register(
            "flashlight_on",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "flashlight_on"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> FLASHLIGHT_OFF = SOUNDS.register(
            "flashlight_off",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "flashlight_off"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMOS = SOUNDS.register(
            "lumos",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "lumos"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> NOX = SOUNDS.register(
            "nox",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SpellsNSquares.MODID, "nox"))
    );
}
