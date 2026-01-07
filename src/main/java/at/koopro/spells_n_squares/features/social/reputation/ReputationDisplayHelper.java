package at.koopro.spells_n_squares.features.social.reputation;

import at.koopro.spells_n_squares.features.social.SocialSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Helper class for displaying reputation information.
 * Provides formatted reputation displays and status messages.
 */
public final class ReputationDisplayHelper {
    
    private ReputationDisplayHelper() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets a formatted reputation display string.
     * 
     * @param player The server player
     * @return Formatted reputation string
     */
    public static Component getReputationDisplay(ServerPlayer player) {
        if (player == null) {
            return Component.literal("Unknown");
        }
        
        SocialSystem.SocialData socialData = SocialSystem.getSocialData(player);
        if (socialData == null) {
            return Component.literal("0");
        }
        
        int reputation = socialData.reputation().reputation();
        return Component.literal("Reputation: " + reputation);
    }
    
    /**
     * Gets a color-coded reputation component.
     * 
     * @param player The server player
     * @return Colored reputation component
     */
    public static Component getColoredReputationDisplay(ServerPlayer player) {
        if (player == null) {
            return Component.literal("Unknown");
        }
        
        SocialSystem.SocialData socialData = SocialSystem.getSocialData(player);
        if (socialData == null) {
            return Component.literal("0").withStyle(net.minecraft.ChatFormatting.GRAY);
        }
        
        int reputation = socialData.reputation().reputation();
        net.minecraft.ChatFormatting color = getReputationColor(reputation);
        
        return Component.literal("Reputation: " + reputation).withStyle(color);
    }
    
    /**
     * Gets color for reputation value.
     */
    private static net.minecraft.ChatFormatting getReputationColor(int reputation) {
        if (reputation < -100) {
            return net.minecraft.ChatFormatting.RED;
        } else if (reputation < 0) {
            return net.minecraft.ChatFormatting.DARK_RED;
        } else if (reputation == 0) {
            return net.minecraft.ChatFormatting.GRAY;
        } else if (reputation < 100) {
            return net.minecraft.ChatFormatting.GREEN;
        } else {
            return net.minecraft.ChatFormatting.DARK_GREEN;
        }
    }
    
    /**
     * Gets a reputation progress bar representation.
     * 
     * @param player The server player
     * @return Progress bar string (e.g., "████░░░░░░")
     */
    public static String getReputationProgressBar(ServerPlayer player) {
        if (player == null) {
            return "░░░░░░░░░░";
        }
        
        SocialSystem.SocialData socialData = SocialSystem.getSocialData(player);
        if (socialData == null) {
            return "░░░░░░░░░░";
        }
        
        int reputation = socialData.reputation().reputation();
        // Normalize reputation to 0-10 scale (-200 to 200 range)
        int progress = Math.max(0, Math.min(10, (reputation + 200) / 40));
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            bar.append(i < progress ? "█" : "░");
        }
        
        return bar.toString();
    }
}

