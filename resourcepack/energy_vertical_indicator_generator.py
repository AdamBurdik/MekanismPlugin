from PIL import Image, ImageChops
import os
import copy
import json

# -- Contants --
path = "./generated/mekanism/assets/mekanism/textures/item/gui/vertical_energy_indicator"
generated_path = "./generated/mekanism/assets/mekanism/textures/item/gui/vertical_energy_indicator"
models_path = "./generated/mekanism/assets/mekanism/models/item/vertical_energy_indicator"
items_path = "./generated/mekanism/assets/mekanism/items"
os.makedirs(models_path, exist_ok=True)

offset = (10, 3)
size = 48

model_schema = {
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


# --------------

template_image = Image.open(f"{path}/template.png")
background_image = Image.open(f"{path}/background.png")

os.makedirs(f"{generated_path}", exist_ok=True)

for y in range(0, size + 1):
    # Texture
    blank = Image.new("RGBA", template_image.size, (64, 64, 64, 255))

    
    blank.paste(background_image, (
        offset[0],
        blank.height - offset[1] - y
    ), background_image)
    
    blank.paste(template_image, (0, 0), template_image)
    
    blank.save(f"{generated_path}/i{y}.png")

    # Model

    model_schema["textures"]["layer0"] = f"mekanism:item/gui/vertical_energy_indicator/i{y}"
    with open(f"{models_path}/i{y}.json", "w") as f:
        json.dump(model_schema, f, indent=4)

    # Model definition
    entry = {
        "when": f"{y}",
        "model": {
            "type": "model",
            "model": f"mekanism:item/vertical_energy_indicator/i{y}"
        }
    }
    item_definition_schema["model"]["cases"].append(entry)

with open(f"{items_path}/vertical_energy_indicator.json", "w") as f:
    json.dump(item_definition_schema, f, indent=4)
