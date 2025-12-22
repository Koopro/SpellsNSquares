package at.koopro.spells_n_squares.features.organizations;

import at.koopro.spells_n_squares.features.playerclass.PlayerClass;
import at.koopro.spells_n_squares.features.playerclass.PlayerClassManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * Manages Ministry of Magic departments.
 * Provides department-specific abilities and permissions.
 */
public final class MinistryDepartmentSystem {
    private MinistryDepartmentSystem() {
    }
    
    // Registry of department members (UUID -> DepartmentMember)
    private static final Map<UUID, DepartmentMember> departmentMembers = new HashMap<>();
    
    /**
     * Ministry departments.
     */
    public enum Department {
        MAGICAL_LAW_ENFORCEMENT("Department of Magical Law Enforcement", "Auror integration, law enforcement"),
        MAGICAL_ACCIDENTS("Department of Magical Accidents and Catastrophes", "Handle magical accidents"),
        CREATURE_REGULATION("Department for the Regulation and Control of Magical Creatures", "Regulate magical creatures"),
        INTERNATIONAL_COOPERATION("Department of International Magical Cooperation", "International relations"),
        MAGICAL_TRANSPORTATION("Department of Magical Transportation", "Manage transportation systems"),
        GAMES_AND_SPORTS("Department of Magical Games and Sports", "Organize games and sports"),
        MYSTERIES("Department of Mysteries", "Research and study mysteries");
        
        private final String displayName;
        private final String description;
        
        Department(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Represents a department member.
     */
    public record DepartmentMember(
        UUID playerId,
        Department department,
        int rank, // 0 = intern, 1 = employee, 2 = senior, 3 = head
        long joinDate,
        Set<String> permissions
    ) {}
    
    /**
     * Assigns a player to a department.
     */
    public static boolean assignToDepartment(ServerPlayer player, Department department) {
        UUID playerId = player.getUUID();
        
        // Remove from old department if exists
        departmentMembers.remove(playerId);
        
        // Add to new department
        Set<String> permissions = getDefaultPermissions(department, 0);
        departmentMembers.put(playerId, new DepartmentMember(
            playerId,
            department,
            0,
            System.currentTimeMillis(),
            permissions
        ));
        
        // Add AUROR class if in Law Enforcement
        if (department == Department.MAGICAL_LAW_ENFORCEMENT) {
            PlayerClassManager.addPlayerClass(player, PlayerClass.AUROR);
        }
        
        return true;
    }
    
    /**
     * Gets the department for a player.
     */
    public static DepartmentMember getDepartmentMember(ServerPlayer player) {
        return departmentMembers.get(player.getUUID());
    }
    
    /**
     * Checks if a player has a specific permission.
     */
    public static boolean hasPermission(ServerPlayer player, String permission) {
        DepartmentMember member = getDepartmentMember(player);
        if (member == null) {
            return false;
        }
        
        return member.permissions().contains(permission);
    }
    
    /**
     * Gets default permissions for a department and rank.
     */
    private static Set<String> getDefaultPermissions(Department department, int rank) {
        Set<String> permissions = new HashSet<>();
        
        switch (department) {
            case MAGICAL_LAW_ENFORCEMENT:
                permissions.add("arrest");
                permissions.add("investigate");
                if (rank >= 1) {
                    permissions.add("use_auror_spells");
                }
                if (rank >= 2) {
                    permissions.add("access_ministry_records");
                }
                break;
            case MAGICAL_ACCIDENTS:
                permissions.add("cleanup_accidents");
                if (rank >= 1) {
                    permissions.add("modify_memory");
                }
                break;
            case CREATURE_REGULATION:
                permissions.add("register_creatures");
                permissions.add("issue_permits");
                break;
            case INTERNATIONAL_COOPERATION:
                permissions.add("diplomatic_access");
                break;
            case MAGICAL_TRANSPORTATION:
                permissions.add("manage_floo_network");
                permissions.add("manage_portkeys");
                break;
            case GAMES_AND_SPORTS:
                permissions.add("organize_quidditch");
                permissions.add("manage_tournaments");
                break;
            case MYSTERIES:
                if (rank >= 2) {
                    permissions.add("access_prophecy_hall");
                    permissions.add("research_time_magic");
                }
                break;
        }
        
        return permissions;
    }
    
    /**
     * Removes a player from their department.
     */
    public static boolean removeFromDepartment(ServerPlayer player) {
        DepartmentMember member = departmentMembers.remove(player.getUUID());
        if (member != null && member.department() == Department.MAGICAL_LAW_ENFORCEMENT) {
            // Remove AUROR class
            PlayerClassManager.removePlayerClass(player, PlayerClass.AUROR);
        }
        return member != null;
    }
}






