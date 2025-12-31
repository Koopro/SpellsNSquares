package at.koopro.spells_n_squares.features.education.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Daily Prophet - news system that displays magical world news.
 * Shows random news articles about the wizarding world.
 */
public class DailyProphetItem extends Item {
    
    private static final String[] NEWS_ARTICLES = {
        "Ministry Announces New Portkey Regulations",
        "Quidditch World Cup Preparations Underway",
        "Hogwarts Students Excel in Transfiguration Exams",
        "Dragon Sanctuary Reports Successful Breeding Season",
        "Auror Office Captures Dark Wizard",
        "New Potion Shop Opens in Diagon Alley",
        "Magical Creature Sightings Increase",
        "Wandmaker Ollivander Celebrates 100 Years",
        "Triwizard Tournament Returns",
        "Magizoologist Discovers New Creature Species"
    };
    
    public DailyProphetItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Vec3 pos = player.position();
            
            // Visual effect
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                pos.x, pos.y + 1.5, pos.z,
                20, 1.0, 1.0, 1.0, 0.1);
            
            level.playSound(null, pos.x, pos.y, pos.z,
                SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, 1.0f);
            
            // Display news
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.daily_prophet.title"));
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.daily_prophet.date"));
            
            // Show random news article
            String article = NEWS_ARTICLES[serverLevel.random.nextInt(NEWS_ARTICLES.length)];
            serverPlayer.sendSystemMessage(Component.translatable("message.spells_n_squares.daily_prophet.article", article));
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}
















