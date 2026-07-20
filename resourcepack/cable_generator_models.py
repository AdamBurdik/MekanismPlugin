import json
import copy
import os

path = "./generated/mekanism/assets/mekanism/models/block/universal_cable"

faces = [
		{
			"name": "north",
			"from": [5, 5, 0],
			"to": [11, 11, 5],
			"rotation": {"angle": 0, "axis": "y", "origin": [8, 8, 2.5]},
			"faces": {
				"east": {"uv": [0, 0, 16, 16], "rotation": 90, "texture": "#1"},
				"west": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"},
				"up": {"uv": [0, 0, 16, 16], "texture": "#1"},
				"down": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"}
			}
		},
		{
			"name": "south",
			"from": [5, 5, 11],
			"to": [11, 11, 16],
			"rotation": {"x": 0, "y": -180, "z": 0, "origin": [8, 8, 13.5]},
			"faces": {
				"east": {"uv": [0, 0, 16, 16], "rotation": 90, "texture": "#1"},
				"west": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"},
				"up": {"uv": [0, 0, 16, 16], "texture": "#1"},
				"down": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"}
			}
		},
		{
			"name": "east",
			"from": [0, 5, 5],
			"to": [5, 11, 11],
			"rotation": {"angle": 0, "axis": "y", "origin": [2.5, 8, 8]},
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "rotation": 90, "texture": "#1"},
				"south": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"},
				"up": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"},
				"down": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"}
			}
		},
		{
			"name": "west",
			"from": [11, 5, 5],
			"to": [16, 11, 11],
			"rotation": {"x": 0, "y": -180, "z": 0, "origin": [13.5, 8, 8]},
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "rotation": 90, "texture": "#1"},
				"south": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"},
				"up": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"},
				"down": {"uv": [0, 0, 16, 16], "rotation": 270, "texture": "#1"}
			}
		},
		{
		    "name": "bottom",
			"from": [5, 0, 5],
			"to": [11, 5, 11],
			"rotation": {"angle": 0, "axis": "y", "origin": [8, 2.5, 8]},
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"},
				"east": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"},
				"south": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"},
				"west": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"}
			}
		},
		{
		    "name": "top",
			"from": [5, 11, 5],
			"to": [11, 16, 11],
			"rotation": {"x": 0, "y": 0, "z": -180, "origin": [8, 13.5, 8]},
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"},
				"east": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"},
				"south": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"},
				"west": {"uv": [0, 0, 16, 16], "rotation": 180, "texture": "#1"}
			}
		}
	]

schema = {
	"format_version": "1.21.11",
	"credit": "Mekanism",
	"texture_size": [32, 32],
	"textures": {
	},
	"elements": [
		{
			"name": "center",
			"from": [5, 5, 5],
			"to": [11, 11, 11],
			"rotation": {"angle": 0, "axis": "y", "origin": [8, 8, 8]},
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#0"},
				"east": {"uv": [0, 0, 16, 16], "texture": "#0"},
				"south": {"uv": [0, 0, 16, 16], "texture": "#0"},
				"west": {"uv": [0, 0, 16, 16], "texture": "#0"},
				"up": {"uv": [0, 0, 16, 16], "texture": "#0"},
				"down": {"uv": [0, 0, 16, 16], "texture": "#0"}
			}
		}
	]
}

tiers = [
    "basic",
    "advanced",
    "elite",
    "ultimate"
]

for tier in tiers:
    os.makedirs(f"{path}/{tier}", exist_ok=True)

    base = copy.deepcopy(schema)
    base["textures"]["0"] = f'mekanism:block/cables/{tier}_universal_cable'
    base["textures"]["1"] = f'mekanism:block/cables/{tier}_universal_cable_vertical'
    base["textures"]["particle"] = f'mekanism:block/{tier}_universal_cable'

    for n in range(64):
        output = copy.deepcopy(base)
        binary = format(n, "06b")
        for i, bit in enumerate(binary):
            if bit == "1":
                output["elements"].append(faces[i])

        with open(f"{path}/{tier}/b{binary}.json", "w") as f:
            json.dump(output, f)

