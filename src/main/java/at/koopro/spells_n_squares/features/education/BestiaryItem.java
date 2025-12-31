package at.koopro.spells_n_squares.features.education;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/**
 * Bestiary - creature information book (Fantastic Beasts style).
 * Opens a GUI screen showing all creatures and allows discovery by scanning nearby creatures.
 */
public class BestiaryItem extends Item {
    
    private static final double SCAN_RANGE = 16.0;
    
    public BestiaryItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        // Initialize creature registry
        BestiaryCreatureRegistry.initialize();
        
        // Scan for nearby creatures and discover them
        Vec3 pos = player.position();
        AABB scanArea = new AABB(pos, pos).inflate(SCAN_RANGE);
        
        var entities = level.getEntitiesOfClass(LivingEntity.class, scanArea,
            entity -> entity != player && entity.isAlive());
        
        Set<Identifier> newlyDiscovered = new HashSet<>();
        
        for (LivingEntity entity : entities) {
            Identifier creatureId = getCreatureIdFromEntity(entity);
            if (creatureId != null) {
                if (!BestiaryData.hasDiscovered(serverPlayer, creatureId)) {
                    BestiaryData.discoverCreature(serverPlayer, creatureId);
                    newlyDiscovered.add(creatureId);
                }
            }
        }
        
        // Visual effect
        serverLevel.sendParticles(ParticleTypes.ENCHANT,
            pos.x, pos.y + 1.0, pos.z,
            20, 1.0, 1.0, 1.0, 0.1);
        
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, 1.0f);
        
        // Show discovery notifications
        if (!newlyDiscovered.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.bestiary.discovered", 
                newlyDiscovered.size()));
            for (Identifier creatureId : newlyDiscovered) {
                BestiaryCreatureRegistry.CreatureEntry entry = BestiaryCreatureRegistry.getCreature(creatureId);
                if (entry != null) {
                    serverPlayer.sendSystemMessage(Component.literal("  - " + entry.getName()));
                }
            }
        }
        
        // Open bestiary GUI
        BestiaryMenuProvider provider = new BestiaryMenuProvider();
        serverPlayer.openMenu(provider);
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Maps an entity to a creature ID for discovery.
     */
    private Identifier getCreatureIdFromEntity(LivingEntity entity) {
        EntityType<?> entityType = entity.getType();
        
        // Map known entity types to creature IDs
        if (entityType == at.koopro.spells_n_squares.features.communication.CommunicationRegistry.OWL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("owl");
        }
        // TODO: Re-enable when these entities are implemented
        /*
        if (entityType == ModEntities.CAT.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("cat");
        } else if (entityType == ModEntities.NIFFLER.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("niffler");
        } else if (entityType == ModEntities.BOWTRUCKLE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("bowtruckle");
        } else if (entityType == ModEntities.PUFFSKEIN.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("puffskein");
        } else if (entityType == ModEntities.KNEAZLE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("kneazle");
        } else if (entityType == ModEntities.ERUMPENT.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("erumpent");
        } else if (entityType == ModEntities.MOONCALF.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("mooncalf");
        } else if (entityType == ModEntities.PHOENIX.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("phoenix");
        } else if (entityType == ModEntities.HOUSE_ELF.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("house_elf");
        } else if (entityType == ModEntities.RAVEN_FAMILIAR.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("raven_familiar");
        } else if (entityType == ModEntities.RAT_FAMILIAR.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("rat_familiar");
        } else if (entityType == ModEntities.SNAKE_FAMILIAR.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("snake_familiar");
        } else if (entityType == ModEntities.FERRET_FAMILIAR.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("ferret_familiar");
        } else if (entityType == ModEntities.AUGUREY.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("augurey");
        } else if (entityType == ModEntities.DEMIGUISE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("demiguise");
        } else if (entityType == ModEntities.FWOOPER.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("fwooper");
        } else if (entityType == ModEntities.JOBBERKNOLL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("jobberknoll");
        } else if (entityType == ModEntities.HIPPOGRIFF.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("hippogriff");
        } else if (entityType == ModEntities.THESTRAL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("thestral");
        } else if (entityType == ModEntities.OCCAMY.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("occamy");
        } else if (entityType == ModEntities.THUNDERBIRD.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("thunderbird");
        } else if (entityType == ModEntities.GRAPHORN.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("graphorn");
        } else if (entityType == ModEntities.ZOUWU.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("zouwu");
        } else if (entityType == ModEntities.DEMENTOR.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("dementor");
        } else if (entityType == ModEntities.BOGGART.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("boggart");
        } else if (entityType == ModEntities.ACROMANTULA.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("acromantula");
        } else if (entityType == ModEntities.DRAGON.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("dragon");
        } else if (entityType == ModEntities.SWOOPING_EVIL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("swooping_evil");
        } else if (entityType == ModEntities.BASILISK.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("basilisk");
        } else if (entityType == ModEntities.CHIMAERA.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("chimaera");
        } else if (entityType == ModEntities.BILLYWIG.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("billywig");
        } else if (entityType == ModEntities.CENTAUR.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("centaur");
        } else if (entityType == ModEntities.CLABBERT.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("clabbert");
        } else if (entityType == ModEntities.DIRICAWL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("diricawl");
        } else if (entityType == ModEntities.FAIRY.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("fairy");
        } else if (entityType == ModEntities.FIRE_CRAB.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("fire_crab");
        } else if (entityType == ModEntities.GHOUL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("ghoul");
        } else if (entityType == ModEntities.GNOME.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("gnome");
        } else if (entityType == ModEntities.HORKLUMP.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("horklump");
        } else if (entityType == ModEntities.IMP.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("imp");
        } else if (entityType == ModEntities.JARVEY.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("jarvey");
        } else if (entityType == ModEntities.LEPRECHAUN.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("leprechaun");
        } else if (entityType == ModEntities.REEM.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("reem");
        } else if (entityType == ModEntities.SPHINX.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("sphinx");
        } else if (entityType == ModEntities.STREELER.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("streeler");
        } else if (entityType == ModEntities.TROLL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("troll");
        } else if (entityType == ModEntities.UNICORN.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("unicorn");
        } else if (entityType == ModEntities.YETI.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("yeti");
        } else if (entityType == ModEntities.ASHWINDER.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("ashwinder");
        } else if (entityType == ModEntities.DOXY.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("doxy");
        } else if (entityType == ModEntities.ERKLING.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("erkling");
        } else if (entityType == ModEntities.LETHIFOLD.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("lethifold");
        } else if (entityType == ModEntities.MANTICORE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("manticore");
        } else if (entityType == ModEntities.NUNDU.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("nundu");
        } else if (entityType == ModEntities.PIXIE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("pixie");
        } else if (entityType == ModEntities.QUINTAPED.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("quintaped");
        } else if (entityType == ModEntities.RED_CAP.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("red_cap");
        } else if (entityType == ModEntities.HUNGARIAN_HORNTAIL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("hungarian_horntail");
        } else if (entityType == ModEntities.CHINESE_FIREBALL.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("chinese_fireball");
        } else if (entityType == ModEntities.SWEDISH_SHORT_SNOUT.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("swedish_short_snout");
        } else if (entityType == ModEntities.COMMON_WELSH_GREEN.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("common_welsh_green");
        } else if (entityType == ModEntities.HEBRIDEAN_BLACK.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("hebridean_black");
        } else if (entityType == ModEntities.PERUVIAN_VIPERTOOTH.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("peruvian_vipertooth");
        } else if (entityType == ModEntities.ROMANIAN_LONGHORN.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("romanian_longhorn");
        } else if (entityType == ModEntities.UKRAINIAN_IRONBELLY.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("ukrainian_ironbelly");
        } else if (entityType == ModEntities.HIPPOCAMPUS.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("hippocampus");
        } else if (entityType == ModEntities.KAPPA.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("kappa");
        } else if (entityType == ModEntities.KELPIE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("kelpie");
        } else if (entityType == ModEntities.MERPEOPLE.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("merpeople");
        } else if (entityType == ModEntities.RAMORA.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("ramora");
        } else if (entityType == ModEntities.GRINDYLOW.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("grindylow");
        } else if (entityType == ModEntities.VEELA.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("veela");
        } else if (entityType == ModEntities.WEREWOLF.get()) {
            return at.koopro.spells_n_squares.core.util.ModIdentifierHelper.modId("werewolf");
        }
        */
        
        return null;
    }
}

















