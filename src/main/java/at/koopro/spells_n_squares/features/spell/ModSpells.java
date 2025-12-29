package at.koopro.spells_n_squares.features.spell;

import at.koopro.spells_n_squares.core.registry.SpellRegistry;
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
     * Registers all spells in the mod.
     * Call this during mod initialization.
     */
    public static void register() {
        // Demo spells for testing - only 5 spells to test all functionality
        // All spells use simple particle effects, no complex entities
        
        // 1. Fireball - Projectile combat spell (uses vanilla fireball entity)
        SpellRegistry.register(new FireballSpell());
        
        // 2. Heal - Healing spell with visual effects
        SpellRegistry.register(new HealSpell());
        
        // 3. Teleport - Movement spell with portal particles
        SpellRegistry.register(new TeleportSpell());
        
        // 4. Periculum - Signal spell with flame particles
        SpellRegistry.register(new PericulumSpell());
        
        // 5. Sonorus - Utility spell with note particles
        SpellRegistry.register(new SonorusSpell());
        
        // Wizarding World Spells
        
        // 6. Expelliarmus - The Disarming Charm
        SpellRegistry.register(new ExpelliarmusSpell());
        
        // 7. Stupefy - The Stunning Spell
        SpellRegistry.register(new StupefySpell());
        
        // 8. Wingardium Leviosa - The Levitation Charm
        SpellRegistry.register(new WingardiumLeviosaSpell());
        
        // 9. Incendio - The Fire-Making Spell
        SpellRegistry.register(new IncendioSpell());
        
        // 10. Aguamenti - The Water-Making Spell
        SpellRegistry.register(new AguamentiSpell());

        // 11. Confringo - Blasting curse with FX-heavy impact
        SpellRegistry.register(new ConfringoSpell());

        // 12. Homenum Revelio - Reveal nearby living entities with glowing outline
        SpellRegistry.register(new HomenumRevelioSpell());
        
        // Utility Spells
        
        // 13. Lumos - Light spell that toggles magical light
        SpellRegistry.register(new LumosSpell());
        
        // 14. Accio - The Summoning Charm
        SpellRegistry.register(new AccioSpell());
        
        // 15. Evanesco - The Vanishing Spell
        SpellRegistry.register(new EvanescoSpell());
        
        // 16. ExtensionCharm - Extends storage capacity
        SpellRegistry.register(new ExtensionCharmSpell());
        
        // 17. ItemRecall - Recalls items to player
        SpellRegistry.register(new ItemRecallSpell());
        
        // 18. PortableCrafting - Portable crafting table spell
        SpellRegistry.register(new PortableCraftingSpell());
        
        // Combat Spells
        
        // 19. Protego - Protective shield spell
        SpellRegistry.register(new ProtegoSpell());
        
        // 20. Lightning - Lightning beam spell
        SpellRegistry.register(new LightningSpell());
        
        // 21. Immobulus - Immobilization spell
        SpellRegistry.register(new ImmobulusSpell());
        
        // 22. Langlock - Tongue-tying spell
        SpellRegistry.register(new LanglockSpell());
        
        // 23. Silencio - Silencing spell
        SpellRegistry.register(new SilencioSpell());
        
        // Movement Spells
        
        // 24. Apparition - Line-of-sight teleportation with Splinching risk
        SpellRegistry.register(new ApparitionSpell());
        
        // 25. Locomotor - Movement charm
        SpellRegistry.register(new LocomotorSpell());
        
        // 26. Levicorpus - Levitation charm (hangs target upside down)
        SpellRegistry.register(new LevicorpusSpell());
        
        // 27. Liberacorpus - Releases target from Levicorpus
        SpellRegistry.register(new LiberacorpusSpell());
        
        // Transformation Spells
        
        // 28. Orchideous - Creates flowers
        SpellRegistry.register(new OrchideousSpell());
        
        // 29. Riddikulus - Boggart-banishing spell
        SpellRegistry.register(new RiddikulusSpell());
        
        // Detection Spells
        
        // 30. EntityDetection - Detects nearby entities
        SpellRegistry.register(new EntityDetectionSpell());
        
        // Other Spells
        
        // 31. Tarantallegra - Dancing feet spell
        SpellRegistry.register(new TarantallegraSpell());
        
        // 32. Muffliato - Privacy charm
        SpellRegistry.register(new MuffliatoSpell());
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
