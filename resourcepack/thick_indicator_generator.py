from PIL import Image, ImageChops, ImageDraw
import os
import copy
import json

# -- Contants --
path = "./generated/mekanism/assets/mekanism/textures/item/gui/thick_indicator"
generated_path = "./generated/mekanism/assets/mekanism/textures/item/gui/thick_indicator"
models_path = "./generated/mekanism/assets/mekanism/models/item/thick_indicator"
items_path = "./generated/mekanism/assets/mekanism/items"
os.makedirs(models_path, exist_ok=True)

offset = 5
size = 23
color = (230, 230, 230)

model_schema = {
    "parent": "minecraft:item/generated",
    "display": {
        "gui": {
            "translation": [
                -9, 0, 0
            ],
            "scale": [
                2.25,
                1.14,
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


# --------------

template = Image.open(f"{path}/template.png")


os.makedirs(f"{generated_path}", exist_ok=True)

for x in range(0, size + 1):
    # Texture
    blank = Image.new("RGBA", template.size, (64, 64, 64, 255))

    draw = ImageDraw.Draw(blank)
    draw.rectangle((0, 0, offset + x, blank.height), fill=color)
    
    blank.paste(template, (0, 0), template)
    
    blank.save(f"{generated_path}/i{x}.png")

    # Model

    model_schema["textures"]["layer0"] = f"mekanism:item/gui/thick_indicator/i{x}"
    with open(f"{models_path}/i{x}.json", "w") as f:
        json.dump(model_schema, f, indent=4)

    # Model definition
    entry = {
        "when": f"{x}",
        "model": {
            "type": "model",
            "model": f"mekanism:item/thick_indicator/i{x}"
        }
    }
    item_definition_schema["model"]["cases"].append(entry)

with open(f"{items_path}/thick_indicator.json", "w") as f:
    json.dump(item_definition_schema, f, indent=4)
