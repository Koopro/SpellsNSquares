# Restructure Script Notes

## Status: Unused

The `restructure.py` script in the project root is currently **unused** and appears to be a planning artifact.

## Purpose

The script was designed to restructure features from feature-based organization to domain-based organization:
- **From**: `features/{feature}/{item|block|system|data|handler|client|network}/...`
- **To**: `{domain}/{feature}/{item|block|system|data|handler|client|network}/...`

Where domains would be: `items`, `blocks`, `systems`, `data`, `handlers`, `client`, `network`, `registries`, `base`, `util`

## Current Organization

The codebase currently uses **feature-based organization**, which is:
- Well-established and working
- Consistent across all features
- Easy to navigate (all code for a feature is in one place)
- Follows domain-driven design principles

## Decision

**Recommendation**: Keep the current feature-based organization. The restructure script can be:
1. **Removed** if not needed
2. **Kept as documentation** if there's a future plan to restructure
3. **Updated** if domain-based organization becomes desirable

## Current Structure Benefits

- All feature code is co-located
- Easy to find all code related to a specific feature
- Clear separation of concerns (features are independent)
- Consistent package structure across features

## If Restructuring is Needed

If domain-based organization is desired in the future:
1. Review the script logic
2. Test on a small feature first
3. Update all imports and references
4. Ensure all tests pass
5. Update documentation







