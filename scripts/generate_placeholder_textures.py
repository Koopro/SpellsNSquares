"""
Script to generate placeholder textures for all tree blocks, items, and blocks.
Creates simple 16x16 solid color PNG files.

Run with: python scripts/generate_placeholder_textures.py
"""

import struct
import zlib
import os
import re

# Wood types and their colors (RGB values)
WOOD_TYPES = {
    "ash": {"planks": (180, 170, 160), "log": (140, 130, 120), "log_top": (200, 190, 180), "leaves": (80, 140, 70)},
    "beech": {"planks": (220, 200, 170), "log": (180, 140, 100), "log_top": (230, 210, 180), "leaves": (70, 150, 60)},
    "blackthorn": {"planks": (60, 50, 50), "log": (40, 35, 35), "log_top": (70, 60, 60), "leaves": (50, 100, 50)},
    "cedar": {"planks": (180, 120, 100), "log": (120, 80, 60), "log_top": (190, 130, 110), "leaves": (60, 130, 60)},
    "chestnut": {"planks": (160, 120, 90), "log": (110, 80, 60), "log_top": (170, 130, 100), "leaves": (80, 140, 60)},
    "cypress": {"planks": (200, 190, 180), "log": (140, 130, 120), "log_top": (210, 200, 190), "leaves": (50, 110, 60)},
    "dogwood": {"planks": (200, 170, 170), "log": (130, 120, 110), "log_top": (210, 180, 180), "leaves": (100, 150, 80)},
    "elder": {"planks": (220, 210, 200), "log": (170, 160, 150), "log_top": (230, 220, 210), "leaves": (120, 160, 100)},
    "elm": {"planks": (190, 150, 110), "log": (130, 100, 70), "log_top": (200, 160, 120), "leaves": (70, 140, 60)},
    "fir": {"planks": (180, 160, 130), "log": (120, 100, 80), "log_top": (190, 170, 140), "leaves": (40, 100, 50)},
    "hawthorn": {"planks": (210, 200, 190), "log": (150, 130, 110), "log_top": (220, 210, 200), "leaves": (90, 150, 70)},
    "holly": {"planks": (200, 180, 170), "log": (130, 110, 100), "log_top": (210, 190, 180), "leaves": (40, 120, 50)},
    "hornbeam": {"planks": (210, 195, 175), "log": (140, 130, 110), "log_top": (220, 205, 185), "leaves": (80, 145, 65)},
    "laurel": {"planks": (170, 150, 120), "log": (110, 90, 70), "log_top": (180, 160, 130), "leaves": (50, 130, 60)},
    "mahogany": {"planks": (140, 70, 60), "log": (100, 50, 40), "log_top": (150, 80, 70), "leaves": (60, 120, 50)},
    "maple": {"planks": (200, 150, 100), "log": (140, 100, 70), "log_top": (210, 160, 110), "leaves": (200, 100, 50)},
    "pear": {"planks": (200, 170, 160), "log": (140, 110, 100), "log_top": (210, 180, 170), "leaves": (80, 150, 70)},
    "pine": {"planks": (210, 190, 140), "log": (150, 120, 80), "log_top": (220, 200, 150), "leaves": (50, 110, 50)},
    "poplar": {"planks": (210, 200, 160), "log": (150, 140, 110), "log_top": (220, 210, 170), "leaves": (100, 160, 80)},
    "redwood": {"planks": (160, 80, 70), "log": (120, 60, 50), "log_top": (170, 90, 80), "leaves": (50, 120, 50)},
    "rowan": {"planks": (190, 140, 100), "log": (130, 90, 60), "log_top": (200, 150, 110), "leaves": (80, 140, 60)},
    "silver_lime": {"planks": (220, 215, 200), "log": (180, 175, 160), "log_top": (230, 225, 210), "leaves": (120, 170, 100)},
    "sycamore": {"planks": (210, 195, 175), "log": (170, 160, 145), "log_top": (220, 205, 185), "leaves": (90, 155, 70)},
    "walnut": {"planks": (120, 90, 70), "log": (80, 60, 45), "log_top": (130, 100, 80), "leaves": (70, 130, 55)},
    "willow": {"planks": (180, 190, 150), "log": (120, 130, 100), "log_top": (190, 200, 160), "leaves": (130, 180, 100)},
    "yew": {"planks": (160, 100, 90), "log": (110, 70, 60), "log_top": (170, 110, 100), "leaves": (40, 100, 45)},
}


def create_png(width, height, color):
    """Create a simple solid color PNG image."""
    
    def create_chunk(chunk_type, data):
        chunk = chunk_type + data
        crc = zlib.crc32(chunk) & 0xffffffff
        return struct.pack(">I", len(data)) + chunk + struct.pack(">I", crc)
    
    # PNG signature
    signature = b'\x89PNG\r\n\x1a\n'
    
    # IHDR chunk
    ihdr_data = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)
    ihdr = create_chunk(b'IHDR', ihdr_data)
    
    # IDAT chunk (image data)
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'  # Filter byte (none)
        for x in range(width):
            raw_data += bytes(color)
    
    compressed = zlib.compress(raw_data, 9)
    idat = create_chunk(b'IDAT', compressed)
    
    # IEND chunk
    iend = create_chunk(b'IEND', b'')
    
    return signature + ihdr + idat + iend


def create_textured_png(width, height, base_color, pattern="solid"):
    """Create a PNG with a simple pattern."""
    
    def create_chunk(chunk_type, data):
        chunk = chunk_type + data
        crc = zlib.crc32(chunk) & 0xffffffff
        return struct.pack(">I", len(data)) + chunk + struct.pack(">I", crc)
    
    # PNG signature
    signature = b'\x89PNG\r\n\x1a\n'
    
    # IHDR chunk
    ihdr_data = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)
    ihdr = create_chunk(b'IHDR', ihdr_data)
    
    # Generate pixel data with pattern
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'  # Filter byte (none)
        for x in range(width):
            r, g, b = base_color
            
            if pattern == "log":
                # Create bark-like vertical stripes
                if (x + y // 2) % 4 == 0:
                    r = max(0, r - 30)
                    g = max(0, g - 30)
                    b = max(0, b - 30)
                elif (x + y // 3) % 6 == 1:
                    r = min(255, r + 15)
                    g = min(255, g + 15)
                    b = min(255, b + 15)
            elif pattern == "log_top":
                # Create ring pattern
                cx, cy = width // 2, height // 2
                dist = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
                if int(dist) % 3 == 0:
                    r = max(0, r - 20)
                    g = max(0, g - 20)
                    b = max(0, b - 20)
            elif pattern == "planks":
                # Create plank pattern
                if y % 4 == 0 or (x + (y // 4) * 7) % 8 == 0:
                    r = max(0, r - 15)
                    g = max(0, g - 15)
                    b = max(0, b - 15)
            elif pattern == "leaves":
                # Create leafy pattern
                if (x + y) % 3 == 0:
                    r = max(0, r - 20)
                    g = min(255, g + 10)
                    b = max(0, b - 10)
                elif (x * y) % 7 == 1:
                    r = min(255, r + 15)
                    g = min(255, g + 15)
                    b = min(255, b + 10)
            
            raw_data += bytes((r, g, b))
    
    compressed = zlib.compress(raw_data, 9)
    idat = create_chunk(b'IDAT', compressed)
    
    # IEND chunk
    iend = create_chunk(b'IEND', b'')
    
    return signature + ihdr + idat + iend


def get_color_for_item(item_name):
    """Determine appropriate color for an item based on its name/type."""
    item_lower = item_name.lower()
    
    # Potions - colored bottles
    if "potion" in item_lower:
        if "healing" in item_lower:
            return (255, 100, 100)  # Red
        elif "strength" in item_lower:
            return (255, 150, 50)  # Orange
        elif "invisibility" in item_lower:
            return (200, 200, 255)  # Light blue
        elif "felix" in item_lower or "felicis" in item_lower:
            return (255, 215, 0)  # Gold
        elif "wolfsbane" in item_lower:
            return (100, 150, 100)  # Green-gray
        elif "veritaserum" in item_lower:
            return (150, 150, 255)  # Light purple
        elif "love" in item_lower:
            return (255, 150, 200)  # Pink
        else:
            return (150, 100, 255)  # Purple
    
    # Currency - gold/silver/copper colors
    if item_lower == "galleon":
        return (255, 215, 0)  # Gold
    elif item_lower == "sickle":
        return (192, 192, 192)  # Silver
    elif item_lower == "knut":
        return (184, 115, 51)  # Copper
    
    # Plants/ingredients - green/brown
    if any(x in item_lower for x in ["plant", "weed", "root", "hair", "feather", "scale", "bezoar"]):
        if "mandrake" in item_lower:
            return (139, 90, 43)  # Brown
        elif "wolfsbane" in item_lower:
            return (100, 150, 100)  # Green-gray
        elif "gillyweed" in item_lower:
            return (50, 150, 50)  # Green
        elif "unicorn" in item_lower:
            return (255, 255, 255)  # White
        elif "dragon" in item_lower:
            return (200, 50, 50)  # Red
        elif "phoenix" in item_lower:
            return (255, 100, 0)  # Orange-red
        elif "dittany" in item_lower:
            return (150, 200, 150)  # Light green
        else:
            return (100, 150, 100)  # Default green
    
    # Artifacts - unique colors
    if "wand" in item_lower:
        return (200, 200, 255)  # Light blue-purple
    elif "cloak" in item_lower:
        return (50, 50, 100)  # Dark blue
    elif "time_turner" in item_lower:
        return (200, 200, 255)  # Light blue
    elif "sneakoscope" in item_lower:
        return (150, 150, 200)  # Blue-gray
    elif "deluminator" in item_lower:
        return (255, 255, 200)  # Light yellow
    elif "remembrall" in item_lower:
        return (255, 200, 200)  # Light red
    elif "sorting_hat" in item_lower:
        return (139, 69, 19)  # Brown
    elif "goblet" in item_lower:
        return (255, 215, 0)  # Gold
    elif "stone" in item_lower:
        return (150, 150, 150)  # Gray
    elif "map" in item_lower:
        return (200, 150, 100)  # Tan
    elif "pensieve" in item_lower:
        return (100, 150, 200)  # Blue
    elif "philosophers" in item_lower:
        return (255, 200, 0)  # Gold
    
    # Storage items
    if "bag" in item_lower:
        return (139, 69, 19)  # Brown
    elif "pocket_dimension" in item_lower:
        return (50, 50, 100)  # Dark blue-purple
    
    # Transportation
    if "broomstick" in item_lower:
        return (139, 90, 43)  # Brown
    elif "portkey" in item_lower:
        return (200, 150, 100)  # Tan
    elif "floo" in item_lower:
        return (100, 150, 255)  # Blue
    
    # Food
    if "butterbeer" in item_lower:
        return (255, 200, 100)  # Light orange
    elif "chocolate" in item_lower:
        return (139, 69, 19)  # Brown
    
    # Quidditch
    if "quaffle" in item_lower:
        return (255, 100, 100)  # Red
    elif "bludger" in item_lower:
        return (100, 100, 100)  # Gray
    elif "snitch" in item_lower:
        return (255, 215, 0)  # Gold
    
    # Tools
    if "hoe" in item_lower:
        return (150, 150, 150)  # Gray
    elif "workbench" in item_lower:
        return (139, 90, 43)  # Brown
    
    # Books
    if "book" in item_lower or "journal" in item_lower:
        return (200, 150, 100)  # Tan
    
    # Robes
    if "robe" in item_lower:
        if "gryffindor" in item_lower:
            return (200, 50, 50)  # Red
        elif "slytherin" in item_lower:
            return (50, 100, 50)  # Green
        elif "hufflepuff" in item_lower:
            return (255, 200, 0)  # Yellow
        elif "ravenclaw" in item_lower:
            return (50, 100, 200)  # Blue
        else:
            return (150, 150, 150)  # Gray
    
    # Default
    return (150, 150, 150)  # Gray


def get_color_for_block(block_name):
    """Determine appropriate color for a block based on its name/type."""
    block_lower = block_name.lower()
    
    # Plants - green
    if "plant" in block_lower or "snare" in block_lower or "tentacula" in block_lower:
        if "mandrake" in block_lower:
            return (139, 90, 43)  # Brown
        elif "wolfsbane" in block_lower:
            return (100, 150, 100)  # Green-gray
        elif "gillyweed" in block_lower:
            return (50, 150, 50)  # Green
        elif "devils" in block_lower:
            return (100, 50, 50)  # Dark red-green
        elif "venomous" in block_lower:
            return (100, 150, 50)  # Yellow-green
        else:
            return (100, 150, 100)  # Default green
    
    # Willow - brown/green
    if "willow" in block_lower:
        return (100, 120, 80)  # Brown-green
    
    # Lights - based on color
    if "light" in block_lower:
        if "white" in block_lower:
            return (255, 255, 255)  # White
        elif "blue" in block_lower:
            return (100, 150, 255)  # Blue
        elif "green" in block_lower:
            return (100, 255, 100)  # Green
        elif "red" in block_lower:
            return (255, 100, 100)  # Red
        elif "purple" in block_lower:
            return (200, 100, 255)  # Purple
        elif "gold" in block_lower:
            return (255, 215, 0)  # Gold
        else:
            return (255, 255, 200)  # Light yellow
    
    # Storage blocks
    if "trunk" in block_lower or "chest" in block_lower:
        return (139, 90, 43)  # Brown
    
    # Automation
    if "cauldron" in block_lower:
        return (100, 100, 100)  # Gray
    elif "furnace" in block_lower:
        return (80, 80, 80)  # Dark gray
    
    # Resource blocks
    if "farm" in block_lower:
        return (150, 200, 150)  # Light green
    elif "collector" in block_lower:
        return (150, 150, 200)  # Light blue
    elif "composter" in block_lower:
        return (139, 90, 43)  # Brown
    elif "generator" in block_lower:
        return (200, 200, 150)  # Light yellow
    
    # Enchantment/education
    if "enchantment" in block_lower:
        return (100, 50, 200)  # Purple
    elif "hourglass" in block_lower:
        return (200, 200, 200)  # Light gray
    
    # Combat
    if "arena" in block_lower:
        return (150, 100, 100)  # Red-gray
    
    # Economy
    if "post" in block_lower or "shop" in block_lower:
        return (200, 150, 100)  # Tan
    elif "vault" in block_lower:
        return (100, 100, 150)  # Blue-gray
    
    # Communication
    if "board" in block_lower:
        return (200, 150, 100)  # Tan
    
    # Default
    return (150, 150, 150)  # Gray


def extract_registered_names(java_file_path):
    """Extract registered item/block names from a Java registry file."""
    names = []
    try:
        with open(java_file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            # Look for patterns like: register("name", ...)
            pattern = r'register\s*\(\s*"([^"]+)"'
            matches = re.findall(pattern, content)
            names.extend(matches)
    except FileNotFoundError:
        print(f"Warning: Could not find {java_file_path}")
    return names


def main():
    # Get the base path
    script_dir = os.path.dirname(os.path.abspath(__file__))
    base_path = os.path.join(script_dir, "..", "src", "main", "resources", "assets", "spells_n_squares", "textures")
    block_path = os.path.join(base_path, "block")
    item_path = os.path.join(base_path, "item")
    
    # Create directories
    os.makedirs(block_path, exist_ok=True)
    os.makedirs(item_path, exist_ok=True)
    
    textures_created = 0
    
    # Generate tree block textures (existing functionality)
    for wood_id, colors in WOOD_TYPES.items():
        print(f"Creating textures for {wood_id}...")
        
        # Log texture (side)
        png = create_textured_png(16, 16, colors["log"], "log")
        with open(os.path.join(block_path, f"{wood_id}_log.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Log top texture
        png = create_textured_png(16, 16, colors["log_top"], "log_top")
        with open(os.path.join(block_path, f"{wood_id}_log_top.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Stripped log (same pattern, lighter)
        stripped_color = tuple(min(255, c + 30) for c in colors["log"])
        png = create_textured_png(16, 16, stripped_color, "log")
        with open(os.path.join(block_path, f"stripped_{wood_id}_log.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Stripped log top
        stripped_top_color = tuple(min(255, c + 30) for c in colors["log_top"])
        png = create_textured_png(16, 16, stripped_top_color, "log_top")
        with open(os.path.join(block_path, f"stripped_{wood_id}_log_top.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Planks texture
        png = create_textured_png(16, 16, colors["planks"], "planks")
        with open(os.path.join(block_path, f"{wood_id}_planks.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Leaves texture
        png = create_textured_png(16, 16, colors["leaves"], "leaves")
        with open(os.path.join(block_path, f"{wood_id}_leaves.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Sapling texture (simple cross shape)
        png = create_png(16, 16, colors["leaves"])
        with open(os.path.join(block_path, f"{wood_id}_sapling.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Door textures
        door_color = tuple(max(0, c - 10) for c in colors["planks"])
        png = create_textured_png(16, 16, door_color, "planks")
        with open(os.path.join(block_path, f"{wood_id}_door_bottom.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        png = create_textured_png(16, 16, door_color, "planks")
        with open(os.path.join(block_path, f"{wood_id}_door_top.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Trapdoor texture
        png = create_textured_png(16, 16, colors["planks"], "planks")
        with open(os.path.join(block_path, f"{wood_id}_trapdoor.png"), "wb") as f:
            f.write(png)
        textures_created += 1
        
        # Door item texture
        png = create_textured_png(16, 16, door_color, "planks")
        with open(os.path.join(item_path, f"{wood_id}_door.png"), "wb") as f:
            f.write(png)
        textures_created += 1
    
    # Extract registered items and blocks from Java files
    mod_items_path = os.path.join(script_dir, "..", "src", "main", "java", "at", "koopro", "spells_n_squares", "core", "registry", "ModItems.java")
    mod_blocks_path = os.path.join(script_dir, "..", "src", "main", "java", "at", "koopro", "spells_n_squares", "core", "registry", "ModBlocks.java")
    
    item_names = extract_registered_names(mod_items_path)
    block_names = extract_registered_names(mod_blocks_path)
    
    # Items that already have textures (skip these)
    existing_item_textures = set()
    if os.path.exists(item_path):
        for filename in os.listdir(item_path):
            if filename.endswith('.png'):
                existing_item_textures.add(filename[:-4])  # Remove .png extension
    
    # Blocks that already have textures (skip these)
    existing_block_textures = set()
    if os.path.exists(block_path):
        for filename in os.listdir(block_path):
            if filename.endswith('.png'):
                existing_block_textures.add(filename[:-4])  # Remove .png extension
    
    # Generate missing item textures
    print("\nGenerating missing item textures...")
    for item_name in item_names:
        if item_name not in existing_item_textures:
            color = get_color_for_item(item_name)
            png = create_png(16, 16, color)
            texture_path = os.path.join(item_path, f"{item_name}.png")
            with open(texture_path, "wb") as f:
                f.write(png)
            textures_created += 1
            print(f"  Created texture for {item_name}")
    
    # Generate missing block textures
    print("\nGenerating missing block textures...")
    for block_name in block_names:
        if block_name not in existing_block_textures:
            color = get_color_for_block(block_name)
            png = create_png(16, 16, color)
            texture_path = os.path.join(block_path, f"{block_name}.png")
            with open(texture_path, "wb") as f:
                f.write(png)
            textures_created += 1
            print(f"  Created texture for {block_name}")
        
        # Some blocks might need additional textures (like _top for whomping_willow)
        if block_name == "whomping_willow" and "whomping_willow_top" not in existing_block_textures:
            color = tuple(max(0, c - 20) for c in get_color_for_block(block_name))
            png = create_png(16, 16, color)
            texture_path = os.path.join(block_path, "whomping_willow_top.png")
            with open(texture_path, "wb") as f:
                f.write(png)
            textures_created += 1
            print(f"  Created texture for whomping_willow_top")
    
    print(f"\nCreated {textures_created} placeholder textures!")
    print("Note: These are simple placeholder textures. Replace with proper artwork later.")


if __name__ == "__main__":
    main()








