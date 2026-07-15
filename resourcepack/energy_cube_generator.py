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

tiers = [
    "basic",
    "advanced",
    "elite",
    "ultimate"
]

for tier in tiers:
    base = copy.deepcopy(schem)

    base["model"]["fallback"]["model"] = f"mekanism:block/energy_cube/{tier}/{tier}_energy_cube_000000"

    for i in range(64):
        binary = format(i, "06b")
        entry = {
                "when": str(i),
                "model": {
                  "type": "minecraft:model",
                  "model": f"mekanism:block/energy_cube/{tier}/{tier}_energy_cube_{binary}"
                }
              }
        base["model"]["cases"].append(entry)

    with open(f'{path}/{tier}_energy_cube.json', 'w') as f:
        json.dump(base, f, indent=4)
        
