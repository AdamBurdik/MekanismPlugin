import json
import os
import copy

# -- Constants --
models_path = "./generated/mekanism/assets/mekanism/models/item/icons/slots"
items_path = "./generated/mekanism/assets/mekanism/items/slots/"

os.makedirs(models_path, exist_ok=True)
os.makedirs(items_path, exist_ok=True)

colors = [
    "none",
    "blue",
    "light_blue",
    "aqua",
    "cyan",
    "green",
    "lime",
    "dark_red",
    "red",
    "magenta",
    "purple",
    "yellow",
    "black"
]
model_schema = {
    "parent": "minecraft:item/generated",
    "display": {
        "gui": {
            "scale": [
                1.11,
                1.14,
                1
            ]
        }
    },
    "textures": {
    }
}
item_definition_schema = {
	"oversized_in_gui": True,
	"model": {
		"type": "minecraft:model",
		"model": "mekanism:item/close_icon"
	}
}


# ---------------

# Models
for color in colors:
    model_schema["textures"]["layer0"] = f"mekanism:item/gui/icons/slots/{color}"

    with open(f"{models_path}/{color}.json", "w") as f:
        json.dump(model_schema, f, indent=4)

# Item definition
for color in colors:
    item_definition_schema["model"]["model"] = f"mekanism:item/icons/slots/{color}"
    with open(f"{items_path}/{color}.json", "w") as f:
        json.dump(item_definition_schema, f, indent=4)
