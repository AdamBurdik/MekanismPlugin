from PIL import Image, ImageChops, ImageDraw
import os
import copy
import json

# -- Contants --
menu_template_path = "./generated/mekanism/assets/mekanism/textures/gui/metallurgic_infuser_template.png"
inf_path = "./generated/mekanism/assets/mekanism/textures/item/gui/infuser_indicator"
inf_generated_path = "./generated/mekanism/assets/mekanism/textures/item/gui/infuser_indicator"
inf_model_path = "./generated/mekanism/assets/mekanism/models/item/infuser_indicator"
items_path = "./generated/mekanism/assets/mekanism/items"

inf_start_pos = (7, 17)
inf_size = (1, 3)
inf_indicator_offset = 3
inf_indicator_size = 48
inf_tiers = [
    "carbon",
    "diamond",
    "golden",
    "redstone"
]
slot_size = 18
inf_model_schema = {
    "parent": "minecraft:item/generated",
    "display": {
        "gui": {
            "translation": [
                0, 18, 0
            ],
            "scale": [
                1.11,
                3.38,
                1
            ]
        }
    },
    "textures": {
        #"layer0": "mekanism:item/energy_indicator/0/energy_indicator_0"
    }
}
item_definition_schema = {
    "oversized_in_gui": True,
    "model": {
        "type": "minecraft:select",
        "property": "minecraft:custom_model_data",
        "cases": []
    }
}

arrow_path = "./generated/mekanism/assets/mekanism/textures/item/gui/infuser_arrow_indicator"
arrow_generated_path = "./generated/mekanism/assets/mekanism/textures/item/gui/infuser_arrow_indicator"
arrow_models_path = "./generated/mekanism/assets/mekanism/models/item/infuser_arrow_indicator"

os.makedirs(arrow_path, exist_ok=True)
os.makedirs(arrow_generated_path, exist_ok=True)
os.makedirs(arrow_models_path, exist_ok=True)

arrow_start_pos = (61, 53)
arrow_size = (3, 1)
arrow_offset = (10, 7)
arrow_indicator_size = 32

arrow_model_schema = {
    "parent": "minecraft:item/generated",
    "display": {
        "gui": {
            "translation": [
                -18, 0, 0
            ],
            "scale": [
                3.38,
                1.14,
                1
            ]
        }
    },
    "textures": {
        #"layer0": "mekanism:item/energy_indicator/0/energy_indicator_0"
    }
}

#levels = [
#    "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"
#]
arrow_color = (255, 255, 255)

# ---------

menu_template_image = Image.open(menu_template_path)

# --- Infussion indicator ---

# Texturex
inf_indicator_template = menu_template_image.crop((
    inf_start_pos[0],
    inf_start_pos[1],
    inf_start_pos[0] + inf_size[0] * slot_size,
    inf_start_pos[1] + inf_size[1] * slot_size,
))

for tier in inf_tiers:
    tier_image = Image.open(f"{inf_path}/{tier}_background.png")

    base = inf_indicator_template.copy()

    for y in range(0, inf_indicator_size + 1): # +1 for 100%

    

        blank = Image.new("RGBA", base.size, (64, 64, 64, 255))

        blank.paste(tier_image, (
            5,
            blank.height - inf_indicator_offset - y
        ), tier_image)

        blank.paste(base, (0, 0), base)

        os.makedirs(f"{inf_generated_path}/{tier}", exist_ok=True)
        blank.save(f"{inf_generated_path}/{tier}/i{y}.png")
        
# Models
for tier in inf_tiers:
    for y in range(0, inf_indicator_size + 1): # +1 for 100%
        base = copy.deepcopy(inf_model_schema)

        base["textures"]["layer0"] = f"mekanism:item/gui/infuser_indicator/{tier}/i{y}"

        os.makedirs(f"{inf_model_path}/{tier}", exist_ok=True)

        with open(f"{inf_model_path}/{tier}/i{y}.json", "w") as f:
            json.dump(base, f, indent=4)

# Item definition
for tier in inf_tiers:
    base = copy.deepcopy(item_definition_schema)

    for y in range(0, inf_indicator_size + 1): # +1 for 100%
        entry = {
            "when": f"{y}",
            "model": {
                "type": "model",
                "model": f"mekanism:item/infuser_indicator/{tier}/i{y}"
            }
        }
        base["model"]["cases"].append(entry)

    os.makedirs(f"{items_path}/infuser_indicator", exist_ok=True)
    with open(f"{items_path}/infuser_indicator/{tier}.json", "w") as f:
        json.dump(base, f, indent=4)


# --- Arrow indicator ---

# Textures
arrow_indicator_template = menu_template_image.crop((
    arrow_start_pos[0],
    arrow_start_pos[1],
    arrow_start_pos[0] + arrow_size[0] * slot_size,
    arrow_start_pos[1] + arrow_size[1] * slot_size,
))

for x in range(0, arrow_indicator_size + 1):
    blank = Image.new("RGBA", arrow_indicator_template.size, (139, 139, 139, 255))

    draw = ImageDraw.Draw(blank)
    draw.rectangle((0, 0, arrow_offset[0] + x, blank.height), fill=arrow_color)

    blank.paste(arrow_indicator_template, (0, 0), arrow_indicator_template)

    blank.save(f"{arrow_generated_path}/i{x}.png")
    
# Models
for x in range(0, arrow_indicator_size + 1):
    base = copy.deepcopy(arrow_model_schema)

    base["textures"]["layer0"] = f"mekanism:item/gui/infuser_arrow_indicator/i{x}"

    with open(f"{arrow_models_path}/i{x}.json", "w") as f:
        json.dump(base, f, indent=4)
        
# Item definition
base = copy.deepcopy(item_definition_schema)
for x in range(0, arrow_indicator_size + 1):
    entry = {
        "when": f"{x}",
        "model": {
            "type": "model",
            "model": f"mekanism:item/infuser_arrow_indicator/i{x}"
        }
    }
    base["model"]["cases"].append(entry)

with open(f"{items_path}/infuser_arrow_indicator.json", "w") as f:
    json.dump(base, f, indent=4)

