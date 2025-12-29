package at.koopro.spells_n_squares.features.npcs;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages NPCs (Non-Player Characters) in the wizarding world.
 * Handles named characters, dialogue, quests, and shops.
 */
public final class NPCSystem {
    private NPCSystem() {
    }
    
    // Registry of NPCs (NPC ID -> NPCData)
    private static final Map<String, NPCData> npcs = new HashMap<>();
    
    // Player interaction history (player UUID -> NPC ID -> interaction count)
    private static final Map<UUID, Map<String, Integer>> interactionHistory = new HashMap<>();
    
    /**
     * Named NPCs from the wizarding world.
     */
    public enum NamedNPC {
        DUMBLEDORE("Dumbledore", "Headmaster of Hogwarts", NPCType.HEADMASTER),
        SNAPE("Severus Snape", "Potions Master", NPCType.TEACHER),
        MCGONAGALL("Minerva McGonagall", "Transfiguration Professor", NPCType.TEACHER),
        HAGRID("Rubeus Hagrid", "Keeper of Keys and Grounds", NPCType.STAFF),
        OLLIVANDER("Garrick Ollivander", "Wandmaker", NPCType.SHOPKEEPER),
        FLAMEL("Nicolas Flamel", "Alchemist", NPCType.SHOPKEEPER),
        LUPIN("Remus Lupin", "Defense Against the Dark Arts Professor", NPCType.TEACHER);
        
        private final String displayName;
        private final String description;
        private final NPCType type;
        
        NamedNPC(String displayName, String description, NPCType type) {
            this.displayName = displayName;
            this.description = description;
            this.type = type;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public NPCType getType() { return type; }
    }
    
    /**
     * NPC types.
     */
    public enum NPCType {
        TEACHER("Teacher", "Can teach spells and provide quests"),
        SHOPKEEPER("Shopkeeper", "Sells items"),
        HEADMASTER("Headmaster", "School administration"),
        STAFF("Staff", "School staff member"),
        STUDENT("Student", "Student NPC");
        
        private final String displayName;
        private final String description;
        
        NPCType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Represents NPC data.
     */
    public record NPCData(
        String npcId,
        String displayName,
        NPCType type,
        List<DialogueOption> dialogues,
        List<Quest> availableQuests,
        ShopData shopData
    ) {}
    
    /**
     * Represents a dialogue option.
     */
    public record DialogueOption(
        String dialogueId,
        String text,
        List<String> responses,
        Map<String, DialogueAction> actions
    ) {}
    
    /**
     * Dialogue actions.
     */
    public enum DialogueAction {
        GIVE_QUEST,
        OPEN_SHOP,
        TEACH_SPELL,
        GIVE_ITEM,
        CHANGE_REPUTATION
    }
    
    /**
     * Represents a quest.
     */
    public record Quest(
        String questId,
        String title,
        String description,
        QuestType type,
        QuestStatus status,
        Map<String, Object> requirements,
        Map<String, Object> rewards
    ) {}
    
    /**
     * Quest types.
     */
    public enum QuestType {
        FETCH("Fetch Quest", "Retrieve items"),
        KILL("Kill Quest", "Defeat enemies"),
        ESCORT("Escort Quest", "Escort NPC"),
        DELIVERY("Delivery Quest", "Deliver items"),
        COLLECTION("Collection Quest", "Collect items");
        
        private final String displayName;
        private final String description;
        
        QuestType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Quest status.
     */
    public enum QuestStatus {
        AVAILABLE,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    
    /**
     * Represents shop data.
     */
    public record ShopData(
        String shopId,
        List<ShopItem> items,
        boolean isOpen
    ) {}
    
    /**
     * Represents a shop item.
     */
    public record ShopItem(
        String itemId,
        int price,
        int stock
    ) {}
    
    /**
     * Registers an NPC.
     */
    public static void registerNPC(NPCData npcData) {
        npcs.put(npcData.npcId(), npcData);
    }
    
    /**
     * Gets NPC data.
     */
    public static NPCData getNPC(String npcId) {
        return npcs.get(npcId);
    }
    
    /**
     * Gets dialogue options for an NPC.
     */
    public static List<DialogueOption> getDialogues(String npcId, ServerPlayer player) {
        NPCData npc = npcs.get(npcId);
        if (npc == null) {
            return List.of();
        }
        
        // Filter dialogues based on player state, reputation, etc.
        return npc.dialogues();
    }
    
    /**
     * Starts a dialogue with an NPC.
     */
    public static boolean startDialogue(ServerPlayer player, String npcId) {
        NPCData npc = npcs.get(npcId);
        if (npc == null) {
            return false;
        }
        
        // TODO: Open dialogue GUI
        // For now, just record interaction
        recordInteraction(player, npcId);
        
        return true;
    }
    
    /**
     * Records an interaction with an NPC.
     */
    private static void recordInteraction(ServerPlayer player, String npcId) {
        interactionHistory.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
            .merge(npcId, 1, Integer::sum);
    }
    
    /**
     * Gets available quests from an NPC.
     */
    public static List<Quest> getAvailableQuests(String npcId, ServerPlayer player) {
        NPCData npc = npcs.get(npcId);
        if (npc == null) {
            return List.of();
        }
        
        return npc.availableQuests().stream()
            .filter(q -> q.status() == QuestStatus.AVAILABLE)
            .toList();
    }
    
    /**
     * Opens an NPC's shop.
     */
    public static boolean openShop(ServerPlayer player, String npcId) {
        NPCData npc = npcs.get(npcId);
        if (npc == null || npc.shopData() == null || !npc.shopData().isOpen()) {
            return false;
        }
        
        // TODO: Open shop GUI
        recordInteraction(player, npcId);
        
        return true;
    }
    
    /**
     * Gets all registered NPCs.
     */
    public static Collection<NPCData> getAllNPCs() {
        return new ArrayList<>(npcs.values());
    }
}















