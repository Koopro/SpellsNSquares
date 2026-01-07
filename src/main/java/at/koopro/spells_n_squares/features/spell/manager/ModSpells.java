package at.koopro.spells_n_squares.features.spell.manager;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
import at.koopro.spells_n_squares.features.spell.base.Spell;
import at.koopro.spells_n_squares.features.spell.combat.ConfringoSpell;
import at.koopro.spells_n_squares.features.spell.combat.ExpelliarmusSpell;
import at.koopro.spells_n_squares.features.spell.combat.FireballSpell;
import at.koopro.spells_n_squares.features.spell.combat.IncendioSpell;
import at.koopro.spells_n_squares.features.spell.combat.LightningSpell;
import at.koopro.spells_n_squares.features.spell.combat.PericulumSpell;
import at.koopro.spells_n_squares.features.spell.combat.ProtegoSpell;
import at.koopro.spells_n_squares.features.spell.combat.StupefySpell;
import at.koopro.spells_n_squares.features.spell.healing.HealSpell;
import at.koopro.spells_n_squares.features.spell.movement.ApparitionSpell;
import at.koopro.spells_n_squares.features.spell.movement.LevicorpusSpell;
import at.koopro.spells_n_squares.features.spell.movement.LiberacorpusSpell;
import at.koopro.spells_n_squares.features.spell.movement.LocomotorSpell;
import at.koopro.spells_n_squares.features.spell.movement.TeleportSpell;
import at.koopro.spells_n_squares.features.spell.movement.WingardiumLeviosaSpell;
import at.koopro.spells_n_squares.features.spell.utility.OrchideousSpell;
import at.koopro.spells_n_squares.features.spell.transformation.RiddikulusSpell;
import at.koopro.spells_n_squares.features.spell.utility.AccioSpell;
import at.koopro.spells_n_squares.features.spell.utility.AguamentiSpell;
import at.koopro.spells_n_squares.features.spell.utility.EntityDetectionSpell;
import at.koopro.spells_n_squares.features.spell.utility.EvanescoSpell;
import at.koopro.spells_n_squares.features.spell.utility.ExtensionCharmSpell;
import at.koopro.spells_n_squares.features.spell.utility.HomenumRevelioSpell;
import at.koopro.spells_n_squares.features.spell.utility.ImmobulusSpell;
import at.koopro.spells_n_squares.features.spell.utility.ItemRecallSpell;
import at.koopro.spells_n_squares.features.spell.utility.LanglockSpell;
import at.koopro.spells_n_squares.features.spell.utility.LumosSpell;
import at.koopro.spells_n_squares.features.spell.utility.MuffliatoSpell;
import at.koopro.spells_n_squares.features.spell.utility.PatronusSpell;
import at.koopro.spells_n_squares.features.spell.utility.PortableCraftingSpell;
import at.koopro.spells_n_squares.features.spell.utility.SilencioSpell;
import at.koopro.spells_n_squares.features.spell.utility.SonorusSpell;
import at.koopro.spells_n_squares.features.spell.utility.TarantallegraSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Registry and initialization of all mod spells.
 * 
 * To create a new spell:
 * 1. Create a class that implements the Spell interface
 * 2. Register it in the register() method below
 * 3. Create an icon texture at: assets/{modid}/textures/spell/{spell_name}.png (16x16 pixels recommended)
 * 
 * For addons: Simply register spells with your mod's namespace, and place icons in:
 * assets/{your_modid}/textures/spell/{spell_id_path}.png
 */
public class ModSpells {
    
    /**
     * Helper method to register a spell with automatic error handling.
     * 
     * @param spell The spell to register
     * @param category Optional category for organization
     */
    private static void registerSpell(Spell spell, String category) {
        try {
            SpellRegistry.register(spell);
            at.koopro.spells_n_squares.core.util.dev.DevLogger.logStateChange(
                ModSpells.class, "registerSpell", 
                "Registered spell: " + spell.getId() + (category != null ? " (Category: " + category + ")" : ""));
        } catch (Exception e) {
            at.koopro.spells_n_squares.core.util.dev.DevLogger.logError(
                ModSpells.class, "registerSpell", 
                "Failed to register spell: " + (spell != null ? spell.getId() : "null"), e);
        }
    }
    
    /**
     * Registers all spells in the mod.
     * Call this during mod initialization.
     */
    public static void register() {
        // Demo spells for testing - only 5 spells to test all functionality
        // All spells use simple particle effects, no complex entities
        
        // Core Demo Spells
        // 1. Fireball - Projectile combat spell (uses vanilla fireball entity)
        registerSpell(new FireballSpell(), "Combat");
        
        // 2. Heal - Healing spell with visual effects
        registerSpell(new HealSpell(), "Healing");
        
        // 3. Teleport - Movement spell with portal particles
        registerSpell(new TeleportSpell(), "Movement");
        
        // 4. Periculum - Signal spell with flame particles
        registerSpell(new PericulumSpell(), "Utility");
        
        // 5. Sonorus - Utility spell with note particles
        registerSpell(new SonorusSpell(), "Utility");
        
        // Wizarding World Spells
        
        // Wizarding World Combat Spells
        // 6. Expelliarmus - The Disarming Charm
        registerSpell(new ExpelliarmusSpell(), "Combat");
        
        // 7. Stupefy - The Stunning Spell
        registerSpell(new StupefySpell(), "Combat");
        
        // 9. Incendio - The Fire-Making Spell
        registerSpell(new IncendioSpell(), "Combat");

        // 11. Confringo - Blasting curse with FX-heavy impact
        registerSpell(new ConfringoSpell(), "Combat");
        
        // 8. Wingardium Leviosa - The Levitation Charm
        registerSpell(new WingardiumLeviosaSpell(), "Movement");
        
        // 10. Aguamenti - The Water-Making Spell
        registerSpell(new AguamentiSpell(), "Utility");

        // 12. Homenum Revelio - Reveal nearby living entities with glowing outline
        registerSpell(new HomenumRevelioSpell(), "Detection");
        
        // Utility Spells
        
        // Utility Spells
        // 13. Lumos - Light spell that toggles magical light
        registerSpell(new LumosSpell(), "Utility");
        
        // 14. Accio - The Summoning Charm
        registerSpell(new AccioSpell(), "Utility");
        
        // 15. Evanesco - The Vanishing Spell
        registerSpell(new EvanescoSpell(), "Utility");
        
        // 16. ExtensionCharm - Extends storage capacity
        registerSpell(new ExtensionCharmSpell(), "Utility");
        
        // 17. ItemRecall - Recalls items to player
        registerSpell(new ItemRecallSpell(), "Utility");
        
        // 18. PortableCrafting - Portable crafting table spell
        registerSpell(new PortableCraftingSpell(), "Utility");
        
        // 23. Langlock - Tongue-tying spell
        registerSpell(new LanglockSpell(), "Utility");
        
        // 24. Silencio - Silencing spell
        registerSpell(new SilencioSpell(), "Utility");
        
        // 32. Tarantallegra - Dancing feet spell
        registerSpell(new TarantallegraSpell(), "Utility");
        
        // 33. Muffliato - Privacy charm
        registerSpell(new MuffliatoSpell(), "Utility");
        
        // Combat Spells
        // 19. Protego - Protective shield spell
        registerSpell(new ProtegoSpell(), "Combat");
        
        // 20. Patronus - Patronus Charm (protection against Dementors)
        registerSpell(new PatronusSpell(), "Combat");
        
        // 21. Lightning - Lightning beam spell
        registerSpell(new LightningSpell(), "Combat");
        
        // 22. Immobulus - Immobilization spell
        registerSpell(new ImmobulusSpell(), "Combat");
        
        // Movement Spells
        // 25. Apparition - Line-of-sight teleportation with Splinching risk
        registerSpell(new ApparitionSpell(), "Movement");
        
        // 26. Locomotor - Movement charm
        registerSpell(new LocomotorSpell(), "Movement");
        
        // 27. Levicorpus - Levitation charm (hangs target upside down)
        registerSpell(new LevicorpusSpell(), "Movement");
        
        // 28. Liberacorpus - Releases target from Levicorpus
        registerSpell(new LiberacorpusSpell(), "Movement");
        
        // Transformation Spells
        // 29. Orchideous - Creates flowers
        registerSpell(new OrchideousSpell(), "Transformation");
        
        // 30. Riddikulus - Boggart-banishing spell
        registerSpell(new RiddikulusSpell(), "Transformation");
        
        // Detection Spells
        // 31. EntityDetection - Detects nearby entities
        registerSpell(new EntityDetectionSpell(), "Detection");
    }
    
    /**
     * Helper class for creating simple spells without implementing all methods.
     * Provides sensible defaults for optional Spell interface methods.
     * 
     * For hold-to-cast spells, override isHoldToCast() to return true
     * and implement onHoldTick() for continuous effects.
     */
    public static abstract class SimpleSpell implements Spell {
        private final Identifier id;
        private final String name;
        private final String description;
        private final int cooldown;
        private final Identifier icon;
        
        public SimpleSpell(Identifier id, String name, String description, int cooldown) {
            this(id, name, description, cooldown, null);
        }
        
        public SimpleSpell(Identifier id, String name, String description, int cooldown, Identifier icon) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.cooldown = cooldown;
            this.icon = icon;
        }
        
        @Override
        public Identifier getId() {
            return id;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public Component getTranslatableName() {
            String translationKey = "spell." + id.getNamespace() + "." + id.getPath() + ".name";
            return Component.translatable(translationKey);
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public int getCooldown() {
            return cooldown;
        }
        
        @Override
        public Identifier getIcon() {
            return icon != null ? icon : Spell.super.getIcon();
        }
        
        @Override
        public void spawnCastEffects(Player player, Level level, boolean success) {
            if (success) {
                // Default: screen flash
                if (level.isClientSide()) {
                    at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerSpellFlash();
                }
                
                // Screen shake for powerful spells
                if (getVisualEffectIntensity() > 0.7f && level.isClientSide()) {
                    at.koopro.spells_n_squares.features.fx.ScreenEffectManager.triggerShake(
                        0.1f * getVisualEffectIntensity(), 10);
                }
            }
        }
    }
}
