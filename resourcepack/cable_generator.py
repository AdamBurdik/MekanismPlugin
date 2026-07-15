import json
import copy

path = "./mekanism/assets/mekanism/items"

schem = {
  "model": {
    "type": "minecraft:select",
    "property": "minecraft:custom_model_data",
    "cases": [],
    "fallback": {
      "type": "minecraft:model"
    }
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

    base["model"]["fallback"]["model"] = f"mekanism:block/cable/{tier}/{tier}_universal_cable_000000"

    for i in range(64):
        binary = format(i, "06b")
        entry = {
                "when": str(i),
                "model": {
                  "type": "minecraft:model",
                  "model": f"mekanism:block/cable/{tier}/{tier}_universal_cable_{binary}"
                }
              }
        base["model"]["cases"].append(entry)

    with open(f'{path}/{tier}_universal_cable.json', 'w') as f:
        json.dump(base, f, indent=4)
        
