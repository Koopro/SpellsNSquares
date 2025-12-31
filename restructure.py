#!/usr/bin/env python3
"""
Script to restructure features from feature-based to domain-based organization.
Run from project root.
"""
import os
import re
import shutil
from pathlib import Path

# Paths relative to project root
BASE_DIR = Path("src/main/java/at/koopro/spells_n_squares")
FEATURES_DIR = BASE_DIR / "features"
DOMAINS = ["items", "blocks", "systems", "data", "handlers", "client", "network", "registries", "base", "util"]

def get_domain_for_file(file_path: Path, feature_name: str) -> str:
    """Determine which domain a file belongs to."""
    file_name = file_path.name
    relative_path = file_path.relative_to(FEATURES_DIR / feature_name)
    
    # Check subdirectories first (most specific)
    if "client" in str(relative_path):
        return "client"
    if "network" in str(relative_path):
        return "network"
    if "base" in str(relative_path):
        return "base"
    if "util" in str(relative_path):
        return "util"
    if "handler" in str(relative_path) or "Handler" in file_name:
        return "handlers"
    if "system" in str(relative_path) or ("System" in file_name and "Registry" not in file_name) or "Manager" in file_name:
        return "systems"
    if "data" in str(relative_path) or file_name.endswith("Data.java"):
        return "data"
    if "block" in str(relative_path) or "Block" in file_name:
        return "blocks"
    if "item" in str(relative_path) or "Item" in file_name:
        return "items"
    if "Registry" in file_name:
        return "registries"
    
    return None

def update_package_declaration(file_path: Path):
    """Update package declaration in a Java file based on its new location."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Calculate new package from file path
        # Path: src/main/java/at/koopro/spells_n_squares/{domain}/{feature}/...
        parts = file_path.parts
        java_idx = parts.index("java")
        package_parts = parts[java_idx + 1:-1]  # Skip filename
        new_package = ".".join(package_parts)
        
        # Replace any old package declaration
        pattern = r'^package\s+at\.koopro\.spells_n_squares\.features\.[^;]+;'
        replacement = f'package {new_package};'
        content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
    except Exception as e:
        print(f"Error updating {file_path}: {e}")

def main():
    """Main restructuring function."""
    if not FEATURES_DIR.exists():
        print(f"Features directory not found: {FEATURES_DIR}")
        print(f"Current directory: {Path.cwd()}")
        return
    
    # Get all feature directories
    features = [d for d in FEATURES_DIR.iterdir() if d.is_dir() and d.name != "creatures"]  # Skip creatures for now
    
    print(f"Found {len(features)} features to restructure (excluding creatures)")
    
    # Process each feature
    for feature_dir in sorted(features):
        feature_name = feature_dir.name
        print(f"\nProcessing feature: {feature_name}")
        
        # Create feature subdirectories in each domain
        for domain in DOMAINS:
            domain_feature_dir = BASE_DIR / domain / feature_name
            domain_feature_dir.mkdir(parents=True, exist_ok=True)
        
        # Find all Java files in this feature
        java_files = list(feature_dir.rglob("*.java"))
        moved_count = 0
        
        for java_file in java_files:
            domain = get_domain_for_file(java_file, feature_name)
            if not domain:
                print(f"  Warning: Could not determine domain for {java_file.relative_to(FEATURES_DIR)}")
                continue
            
            # Calculate relative path within feature
            relative_path = java_file.relative_to(feature_dir)
            
            # Determine new location
            if relative_path.parent == Path("."):
                # File is in feature root
                new_path = BASE_DIR / domain / feature_name / java_file.name
            else:
                # File is in a subdirectory
                parent = relative_path.parent
                # Skip domain subdirectories but keep feature-specific subdirectories
                if parent.name in ["client", "network", "base", "util", "handler", "system", "data", "block", "item"]:
                    new_path = BASE_DIR / domain / feature_name / java_file.name
                else:
                    # Preserve subdirectory structure (e.g., creatures/aquatic/)
                    new_path = BASE_DIR / domain / feature_name / relative_path
            
            # Create parent directories
            new_path.parent.mkdir(parents=True, exist_ok=True)
            
            # Move file
            if java_file != new_path:
                print(f"  Moving {relative_path} -> {domain}/{feature_name}/")
                shutil.move(str(java_file), str(new_path))
                update_package_declaration(new_path)
                moved_count += 1
        
        print(f"  Moved {moved_count} files")
        
        # Remove empty subdirectories
        for subdir in sorted(feature_dir.rglob("*"), reverse=True):
            if subdir.is_dir() and not any(subdir.rglob("*.java")):
                try:
                    subdir.rmdir()
                except:
                    pass
    
    print("\nRestructuring complete!")

if __name__ == "__main__":
    main()
