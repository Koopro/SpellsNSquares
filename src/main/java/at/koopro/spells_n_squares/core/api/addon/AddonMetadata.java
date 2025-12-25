package at.koopro.spells_n_squares.core.api.addon;

import java.util.List;

/**
 * Metadata about an addon.
 * Contains information about the addon's identity, version, and dependencies.
 */
public final class AddonMetadata {
    private final String addonId;
    private final String addonName;
    private final String addonVersion;
    private final String minApiVersion;
    private final List<String> dependencies;
    
    public AddonMetadata(String addonId, String addonName, String addonVersion, String minApiVersion, List<String> dependencies) {
        this.addonId = addonId;
        this.addonName = addonName;
        this.addonVersion = addonVersion;
        this.minApiVersion = minApiVersion;
        this.dependencies = List.copyOf(dependencies);
    }
    
    public String getAddonId() {
        return addonId;
    }
    
    public String getAddonName() {
        return addonName;
    }
    
    public String getAddonVersion() {
        return addonVersion;
    }
    
    public String getMinApiVersion() {
        return minApiVersion;
    }
    
    public List<String> getDependencies() {
        return dependencies;
    }
}

















