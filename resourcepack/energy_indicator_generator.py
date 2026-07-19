from PIL import Image, ImageChops
import os
import copy
import json

path = "./mekanism/assets/mekanism/textures/gui/energy_cube_template.png"
output = "./mekanism/assets/mekanism/textures/item/energy_indicator"
items = "./mekanism/assets/mekanism/items"
models = "./mekanism/assets/mekanism/models/item"
background_path = "./mekanism/assets/mekanism/textures/item/energy_indicator/background"

os.makedirs(output, exist_ok=True)

menu = Image.open(path)

start_pos = (43, 17)
slot_size = 18


offsets = [
    3,
    0,
    -3
]

levels = [
    "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"
]

item_model_schema = {
    "parent": "minecraft:item/generated",
    "display": {
      "gui": {
        "scale": [1.11, 1.14, 1]
      }  
    },
    "textures": {
    }
}

schema = {
    "oversized_in_gui": True,
    "model": {
        "type": "minecraft:select",
        "property": "minecraft:custom_model_data",
        "cases": []
    }
}

background_template = Image.open(f"{background_path}/template.png")

# Generate background textures
for level in levels:
    pct = int(level)
    offset = round(pct * slot_size / 100)

    #print(offset)
    print(slot_size - offset)

    empty = Image.new("RGBA", (slot_size, slot_size), (0, 0, 0, 0))

    empty.paste(background_template, (0, slot_size - offset), background_template)

    empty.save(f"{background_path}/{level}.png")
    


# 0b  0000  0000

for li, level in enumerate(levels):
    level_slot = Image.open(f"{output}/background/{level}.png")

    os.makedirs(f"{output}/{level}", exist_ok=True)

    i = 0
    for y in range(0, 3):
        for x in range(0, 5):    
            os.makedirs(f"{models}/energy_indicator/{level}", exist_ok=True)
            
            if level == "0":
                cropped = menu.crop(
                    (
                        start_pos[0] + x * slot_size,
                        start_pos[1] + y * slot_size,
                        start_pos[0] + x * slot_size + slot_size,
                        start_pos[1] + y * slot_size + slot_size,
                    )
                )
                cropped.save(f"{output}/0/energy_indicator_{i}.png")
            else:
                # Generate texture    
                slot = Image.open(f"{output}/0/energy_indicator_{i}.png")

                bg = level_slot.copy()
                offset_bg = ImageChops.offset(bg, 0, offsets[y])

                offset_bg.paste(slot, (0, 0), slot)

                offset_bg.save(f"{output}/{level}/energy_indicator_{i}.png")

            # Generate model file
            base = copy.deepcopy(item_model_schema)
            base["textures"]["layer0"] = f"mekanism:item/energy_indicator/{level}/energy_indicator_{i}"

            with open(f"{models}/energy_indicator/{level}/energy_indicator_{i}.json", "w") as f:
                json.dump(base, f, indent=4)

            cmd = i << 4 | li

            entry = {
                "when": str(cmd),
                "model": {
                    "type": "minecraft:model",
                    "model": f"mekanism:item/energy_indicator/{level}/energy_indicator_{i}"
                }
            }

            schema["model"]["cases"].append(entry)

            i += 1

with open(f"{items}/energy_indicator.json", "w") as f:
    json.dump(schema, f, indent=4)
            
