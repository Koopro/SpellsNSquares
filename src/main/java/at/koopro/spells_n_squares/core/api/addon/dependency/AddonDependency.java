package at.koopro.spells_n_squares.core.api.addon.dependency;

import java.util.Objects;

/**
 * Represents a dependency requirement for an addon.
 */
public final class AddonDependency {
    private final String modId;
    private final String versionRange; // e.g., ">=1.0.0 <2.0.0" or just "1.0.0"
    private final boolean required;
    
    public AddonDependency(String modId, String versionRange, boolean required) {
        this.modId = modId;
        this.versionRange = versionRange != null ? versionRange : "";
        this.required = required;
    }
    
    public AddonDependency(String modId, boolean required) {
        this(modId, null, required);
    }
    
    public String getModId() {
        return modId;
    }
    
    public String getVersionRange() {
        return versionRange;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public boolean hasVersionRequirement() {
        return !versionRange.isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddonDependency that = (AddonDependency) o;
        return required == that.required && Objects.equals(modId, that.modId) && Objects.equals(versionRange, that.versionRange);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(modId, versionRange, required);
    }
    
    @Override
    public String toString() {
        if (versionRange.isEmpty()) {
            return modId + (required ? "" : " (optional)");
        }
        return modId + "@" + versionRange + (required ? "" : " (optional)");
    }
}

