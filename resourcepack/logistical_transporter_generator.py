import os
import json
import copy

path = "./generated/mekanism/assets/mekanism/items"

schem = {
  "model": {
    "type": "minecraft:select",
    "property": "minecraft:custom_model_data",
    "cases": []
  }
}

entries = [
    "basic",
    "advanced",
    "elite",
    "ultimate"
]

for tier in entries:
    base = copy.deepcopy(schem)

    for i in range(64):
        binary = format(i, "06b")
        entry = {
                "when": str(i),
                "model": {
                  "type": "minecraft:model",
                  "model": f"mekanism:block/logistical_transporter/{tier}/b{binary}"
                }
              }
        base["model"]["cases"].append(entry)

    os.makedirs(f'{path}/logistical_transporter', exist_ok=True)
    with open(f'{path}/logistical_transporter/{tier}.json', 'w') as f:
        json.dump(base, f, indent=4)
        
