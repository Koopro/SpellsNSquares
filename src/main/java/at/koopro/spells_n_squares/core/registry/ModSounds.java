package at.koopro.spells_n_squares.core.registry;

import at.koopro.spells_n_squares.SpellsNSquares;
import at.koopro.spells_n_squares.core.util.ModIdentifierHelper;
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
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("rubber_duck_squeak"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> FLASHLIGHT_ON = SOUNDS.register(
            "flashlight_on",
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("flashlight_on"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> FLASHLIGHT_OFF = SOUNDS.register(
            "flashlight_off",
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("flashlight_off"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMOS = SOUNDS.register(
            "lumos",
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("lumos"))
    );
    
    public static final DeferredHolder<SoundEvent, SoundEvent> NOX = SOUNDS.register(
            "nox",
            () -> SoundEvent.createVariableRangeEvent(ModIdentifierHelper.modId("nox"))
    );
}
