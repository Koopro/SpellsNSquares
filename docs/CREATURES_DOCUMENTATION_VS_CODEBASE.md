# Creatures: Documentation vs Codebase Comparison

This document compares the creatures documented in the Docusaurus documentation (Volume IV: Menagerie) with what's actually implemented in the codebase.

**Last Updated:** 2025-01-26

---

## Summary

- **Total Creatures in Documentation:** ~92 unique creatures (including variants)
- **Total Creatures in Codebase:** 77 registered creature types
- **Creatures in Codebase but Not Documented:** To be determined
- **Creatures Documented but Not in Codebase:** ~30+ creatures

---

## Creatures Documented but NOT in Codebase

### Harmless (XX) Creatures Missing:
1. **Auger** - Not found in codebase
2. **Chizpurfle** - Not found in codebase
3. **Flobberworm** - Not found in codebase
4. **Giant Purple Toad** - Codebase has "toad" but not specifically "giant-purple-toad" variant

### Competent (XXX) Creatures Missing:
5. **Bundimun** - Not found in codebase
6. **Crup** - Not found in codebase
7. **Dugbog** - Not found in codebase
8. **Glumbumble** - Not found in codebase
9. **Knarl** - Not found in codebase
10. **Mackled Malaclaw** - Not found in codebase
11. **Moke** - Not found in codebase
12. **Murtlap** - Not found in codebase
13. **Nogtail** - Not found in codebase
14. **Plimpy** - Not found in codebase
15. **Pogrebin** - Not found in codebase
16. **Salamander** - Not found in codebase
17. **Shrake** - Not found in codebase

### Dangerous (XXXX) Creatures Missing:
18. **Griffin** - Not found in codebase
19. **Runespoor** - Not found in codebase
20. **Snallygaster** - Not found in codebase
21. **Tebo** - Not found in codebase
22. **Wampus Cat** - Not found in codebase

### Wizard Killer (XXXXX) Creatures Missing:
23. **Dragon - Norwegian Ridgeback** - Codebase has 8 dragon breeds, but Norwegian Ridgeback is missing
24. **Wampus Cat** - Not found in codebase (also listed as dangerous)

### Spirits & Humanoids Missing:
25. **Giant** - Not found in codebase
26. **Goblin** - Not found in codebase
27. **House Elf** - Not found in codebase
28. **Inferius** - Not found in codebase
29. **Poltergeist (Peeves)** - Not found in codebase
30. **Ghost (Standard)** - Codebase has `GhostEntity` and `HouseGhostEntity`, but may not match documentation exactly

### Variants/Subtypes Missing:
31. **Merpeople - Selkie** - Codebase has generic "merpeople" but not selkie variant
32. **Merpeople - Siren** - Codebase has generic "merpeople" but not siren variant
33. **Troll - Forest** - Codebase has generic "troll" but not forest variant
34. **Troll - Mountain** - Codebase has generic "troll" but not mountain variant
35. **Troll - River** - Codebase has generic "troll" but not river variant

---

## Creatures in Codebase but NOT in Documentation

### Companion Creatures:
1. **Phoenix** - Found in codebase (`PhoenixEntity.java`) but may be documented under a different name
2. **Owl** - In codebase, may be documented differently
3. **Cat** - In codebase, may be documented differently
4. **Raven Familiar** - In codebase, may be documented differently
5. **Rat Familiar** - In codebase, may be documented differently
6. **Snake Familiar** - In codebase, may be documented differently
7. **Ferret Familiar** - In codebase, may be documented differently

*Note: These familiars might be documented under different names or categories.*

---

## Creatures with Name/Variant Differences

### Matched but Different Naming:
1. **Pixie** - Codebase has `PixieEntity`, documentation has "pixie-cornish"
2. **Puffskein** - Codebase has "puffskein", documentation has "puffskein-pygmy-puff" (may include variant)
3. **Toad** - Codebase has generic "toad", documentation has "giant-purple-toad"
4. **Troll** - Codebase has generic "troll", documentation has three variants (forest, mountain, river)
5. **Merpeople** - Codebase has generic "merpeople", documentation has two variants (selkie, siren)
6. **Dragon Breeds** - Codebase has 8 breeds, documentation has 9 (includes Norwegian Ridgeback)

---

## Creatures Fully Matched

The following creatures appear in both documentation and codebase:

### Harmless (XX):
- Augurey ✓
- Bowtruckle ✓
- Clabbert ✓
- Diricawl ✓
- Ghoul ✓
- Gnome ✓
- Horklump ✓
- Imp ✓
- Jobberknoll ✓
- Mooncalf ✓
- Puffskein ✓ (variant)
- Ramora ✓

### Competent (XXX):
- Ashwinder ✓
- Billywig ✓
- Doxy ✓
- Fire Crab ✓
- Fwooper ✓
- Hippocampus ✓
- Jarvey ✓
- Kneazle ✓
- Leprechaun ✓
- Niffler ✓
- Pixie ✓ (variant naming)
- Red Cap ✓
- Streeler ✓

### Dangerous (XXXX):
- Centaur ✓
- Demiguise ✓
- Erumpent ✓
- Graphorn ✓
- Hippogriff ✓
- Kappa ✓
- Kelpie ✓
- Merpeople ✓ (generic, variants missing)
- Occamy ✓
- Phoenix ✓
- Re'em ✓
- Sphinx ✓
- Thestral ✓
- Thunderbird ✓
- Troll ✓ (generic, variants missing)
- Unicorn ✓
- Yeti ✓

### Wizard Killer (XXXXX):
- Acromantula ✓
- Basilisk ✓
- Chimaera ✓
- Dragon - Chinese Fireball ✓
- Dragon - Common Welsh Green ✓
- Dragon - Hebridean Black ✓
- Dragon - Hungarian Horntail ✓
- Dragon - Peruvian Vipertooth ✓
- Dragon - Romanian Longhorn ✓
- Dragon - Swedish Short-Snout ✓
- Dragon - Ukrainian Ironbelly ✓
- Lethifold ✓
- Manticore ✓
- Nundu ✓
- Quintaped ✓
- Werewolf ✓

### Spirits & Humanoids:
- Dementor ✓
- Veela ✓

---

## Recommendations

### High Priority Missing Creatures:
1. **Norwegian Ridgeback Dragon** - Complete the dragon breed collection
2. **Griffin** - Popular creature from the wizarding world
3. **Giant, Goblin, House Elf** - Important humanoid creatures
4. **Poltergeist (Peeves)** - Iconic Hogwarts character

### Medium Priority Missing Creatures:
5. **Crup, Knarl, Moke** - Common magical creatures
6. **Salamander, Runespoor** - Interesting creatures with unique mechanics
7. **Troll Variants** - Add forest, mountain, and river troll variants
8. **Merpeople Variants** - Add selkie and siren variants

### Low Priority Missing Creatures:
9. **Bundimun, Chizpurfle, Flobberworm** - Less common creatures
10. **Dugbog, Glumbumble, Mackled Malaclaw** - Niche creatures
11. **Murtlap, Nogtail, Plimpy, Pogrebin** - Rare creatures
12. **Shrake, Snallygaster, Tebo, Wampus Cat** - Regional/rare creatures

---

## Notes

- The codebase uses a simplified categorization system (COMPANION, NEUTRAL, MOUNT, HOSTILE, AQUATIC, SPIRITUAL, SPECIAL) while the documentation uses the Ministry of Magic classification system (XX, XXX, XXXX, XXXXX).
- Some creatures in the codebase may be documented under different names or in different volumes.
- Variants (like troll types, merpeople types) may be implemented as a single entity with different properties rather than separate entities.
- This comparison is based on the `ModCreatures.java` registry file and entity class names found in the codebase.

---

*This document should be updated as new creatures are implemented or documentation is updated.*












