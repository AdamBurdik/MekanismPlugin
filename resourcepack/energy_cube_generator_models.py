import json
import copy
import os

path = "./generated/mekanism/assets/mekanism/models/block/energy_cube"

faces = [
    [
        {
			"name": "port_bg_north",
			"from": [3, 5, 1],
			"to": [13, 11, 2],
			"rotation": {"angle": 0, "axis": "y", "origin": [8, 8, 1.5]},
			"faces": {
				"north": {"uv": [0, 0, 10, 6], "texture": "#1"},
				"south": {"uv": [0, 0, 10, 6], "texture": "#1"},
				"up": {"uv": [0, 0, 10, 1], "texture": "#1"},
				"down": {"uv": [0, 0, 10, 1], "texture": "#1"}
			}
		},
		{
			"name": "port_north",
			"from": [4, 4, 0],
			"to": [12, 12, 1],
			"rotation": {"angle": 0, "axis": "y", "origin": [4, 6, 2]},
			"faces": {
				"north": {"uv": [0, 0, 8, 8], "texture": "#2"},
				"east": {"uv": [0, 0, 1, 8], "texture": "#2"},
				"south": {"uv": [8, 0, 16, 8], "texture": "#2"},
				"west": {"uv": [7, 0, 8, 8], "texture": "#2"},
				"up": {"uv": [0, 0, 8, 1], "texture": "#2"},
				"down": {"uv": [0, 7, 8, 8], "texture": "#2"}
			}
		},
		{
			"name": "port_overlay_north",
			"from": [4, 4, 0],
			"to": [12, 12, 0],
			"rotation": {"angle": 0, "axis": "y", "origin": [4, 6, 2]},
			"faces": {
				"north": {"uv": [8, 8, 16, 16], "texture": "#3"}
			}
		}
    ],
    [
        {
        	"name": "port_bg_south",
        	"from": [3, 5, 14],
        	"to": [13, 11, 15],
        	"rotation": {"x": 0, "y": -180, "z": 0, "origin": [8, 8, 14.5]},
        	"faces": {
        		"north": {"uv": [0, 0, 10, 6], "texture": "#1"},
        		"south": {"uv": [0, 0, 10, 6], "texture": "#1"},
        		"up": {"uv": [0, 0, 10, 1], "texture": "#1"},
        		"down": {"uv": [0, 0, 10, 1], "texture": "#1"}
        	}
        },
        {
        	"name": "port_south",
        	"from": [4, 4, 15],
        	"to": [12, 12, 16],
        	"rotation": {"x": 0, "y": -180, "z": 0, "origin": [8, 8, 15.5]},
        	"faces": {
        		"north": {"uv": [0, 0, 8, 8], "texture": "#2"},
        		"east": {"uv": [0, 0, 1, 8], "texture": "#2"},
        		"south": {"uv": [8, 0, 16, 8], "texture": "#2"},
        		"west": {"uv": [7, 0, 8, 8], "texture": "#2"},
        		"up": {"uv": [0, 0, 8, 1], "texture": "#2"},
        		"down": {"uv": [0, 7, 8, 8], "texture": "#2"}
        	}
        },
        {
        	"name": "port_overlay_south",
        	"from": [4, 4, 16],
        	"to": [12, 12, 16],
        	"rotation": {"x": 0, "y": -180, "z": 0, "origin": [8, 8, 16]},
        	"faces": {
        		"north": {"uv": [8, 8, 16, 16], "texture": "#3"}
        	}
        },
    ],
    [
        {
        	"name": "port_bg_west",
        	"from": [1, 5, 3],
        	"to": [2, 11, 13],
        	"rotation": {"x": 0, "y": -180, "z": 0, "origin": [1.5, 8, 8]},
        	"faces": {
        		"east": {"uv": [0, 0, 10, 6], "texture": "#1"},
        		"west": {"uv": [0, 0, 10, 6], "texture": "#1"},
        		"up": {"uv": [0, 0, 10, 1], "texture": "#1"},
        		"down": {"uv": [0, 0, 10, 1], "texture": "#1"}
        	}
        },
        {
        	"name": "port_west",
        	"from": [-4, 4, 8],
        	"to": [4, 12, 9],
        	"rotation": {"x": 0, "y": 90, "z": 0, "origin": [0, 8, 8]},
        	"faces": {
        		"north": {"uv": [0, 0, 8, 8], "texture": "#2"},
        		"east": {"uv": [0, 0, 1, 8], "texture": "#2"},
        		"south": {"uv": [8, 0, 16, 8], "texture": "#2"},
        		"west": {"uv": [7, 0, 8, 8], "texture": "#2"},
        		"up": {"uv": [0, 0, 8, 1], "texture": "#2"},
        		"down": {"uv": [0, 7, 8, 8], "texture": "#2"}
        	}
        },
        {
        	"name": "port_overlay_west",
        	"from": [-4, 4, 8],
        	"to": [4, 12, 8],
        	"rotation": {"x": 0, "y": 90, "z": 0, "origin": [0, 8, 8]},
        	"faces": {
        		"north": {"uv": [8, 8, 16, 16], "texture": "#3"}
        	}
        },
    ],
    [
        {
        	"name": "port_bg_east",
        	"from": [14, 5, 3],
        	"to": [15, 11, 13],
        	"rotation": {"angle": 0, "axis": "y", "origin": [14.5, 8, 8]},
        	"faces": {
        		"east": {"uv": [0, 0, 10, 6], "texture": "#1"},
        		"west": {"uv": [0, 0, 10, 6], "texture": "#1"},
        		"up": {"uv": [0, 0, 10, 1], "texture": "#1"},
        		"down": {"uv": [0, 0, 10, 1], "texture": "#1"}
        	}
        },
        {
        	"name": "port_east",
        	"from": [11.5, 4, 7.5],
        	"to": [19.5, 12, 8.5],
        	"rotation": {"x": 0, "y": -90, "z": 0, "origin": [15.5, 8, 8]},
        	"faces": {
        		"north": {"uv": [0, 0, 8, 8], "texture": "#2"},
        		"east": {"uv": [0, 0, 1, 8], "texture": "#2"},
        		"south": {"uv": [8, 0, 16, 8], "texture": "#2"},
        		"west": {"uv": [7, 0, 8, 8], "texture": "#2"},
        		"up": {"uv": [0, 0, 8, 1], "texture": "#2"},
        		"down": {"uv": [0, 7, 8, 8], "texture": "#2"}
        	}
        },
        {
        	"name": "port_overlay_east",
        	"from": [12, 4, 8],
        	"to": [20, 12, 8],
        	"rotation": {"x": 0, "y": -90, "z": 0, "origin": [16, 8, 8]},
        	"faces": {
        		"north": {"uv": [8, 8, 16, 16], "texture": "#3"}
        	}
        },
    ],
    [
        {
        	"name": "port_bg_down",
        	"from": [5, 1, 3],
        	"to": [11, 2, 13],
        	"rotation": {"angle": 0, "axis": "y", "origin": [4, 1, 4]},
        	"faces": {
        		"east": {"uv": [0, 0, 10, 1], "rotation": 90, "texture": "#1"},
        		"west": {"uv": [0, 0, 10, 1], "rotation": 90, "texture": "#1"},
        		"up": {"uv": [0, 0, 10, 6], "rotation": 90, "texture": "#1"},
        		"down": {"uv": [0, 0, 10, 6], "rotation": 90, "texture": "#1"}
        	}
        },
        {
        	"name": "port_down",
        	"from": [4, -4, 8],
        	"to": [12, 4, 9],
        	"rotation": {"x": 0, "y": 90, "z": 90, "origin": [8, 0, 8]},
        	"faces": {
        		"north": {"uv": [0, 0, 8, 8], "texture": "#2"},
        		"east": {"uv": [0, 0, 1, 8], "texture": "#2"},
        		"south": {"uv": [8, 0, 16, 8], "texture": "#2"},
        		"west": {"uv": [7, 0, 8, 8], "texture": "#2"},
        		"up": {"uv": [0, 0, 8, 1], "texture": "#2"},
        		"down": {"uv": [0, 7, 8, 8], "texture": "#2"}
        	}
        },
        {
        	"name": "port_overlay_down",
        	"from": [4, -4, 8],
        	"to": [12, 4, 8],
        	"rotation": {"x": 0, "y": 90, "z": 90, "origin": [8, 0, 8]},
        	"faces": {
        		"north": {"uv": [8, 8, 16, 16], "texture": "#3"}
        	}
        },
    ],
    [
        {
        	"name": "port_bg_up",
        	"from": [5, 14, 3],
        	"to": [11, 15, 13],
        	"rotation": {"angle": 0, "axis": "y", "origin": [4, 14, 4]},
        	"faces": {
        		"east": {"uv": [0, 0, 10, 1], "rotation": 90, "texture": "#1"},
        		"west": {"uv": [0, 0, 10, 1], "rotation": 90, "texture": "#1"},
        		"up": {"uv": [0, 0, 10, 6], "rotation": 90, "texture": "#1"},
        		"down": {"uv": [0, 0, 10, 6], "rotation": 90, "texture": "#1"}
        	}
        },
        {
        	"name": "port_up",
        	"from": [4, 12, 8],
        	"to": [12, 20, 9],
        	"rotation": {"x": 0, "y": 90, "z": -90, "origin": [8, 16, 8]},
        	"faces": {
        		"north": {"uv": [0, 0, 8, 8], "texture": "#2"},
        		"east": {"uv": [0, 0, 1, 8], "texture": "#2"},
        		"south": {"uv": [8, 0, 16, 8], "texture": "#2"},
        		"west": {"uv": [7, 0, 8, 8], "texture": "#2"},
        		"up": {"uv": [0, 0, 8, 1], "texture": "#2"},
        		"down": {"uv": [0, 7, 8, 8], "texture": "#2"}
        	}
        },
        {
        	"name": "port_overlay_up",
        	"from": [4, 12, 8],
        	"to": [12, 20, 8],
        	"rotation": {"x": 0, "y": 90, "z": -90, "origin": [8, 16, 8]},
        	"faces": {
        		"north": {"uv": [8, 8, 16, 16], "texture": "#3"}
        	}
        },

    ]
]

schema = {
	"format_version": "1.21.11",
	"credit": "Mekanism",
	"textures": {
		"1": "mekanism:block/energy_cube",
		"2": "mekanism:block/ports",
		"3": "mekanism:block/ports_led",
		"6": "mekanism:block/corner_overlay",
		"particle": "mekanism:block/steel_casing"
	},
	"elements": [
		{
			"name": "frame6",
			"from": [0, 3, 13],
			"to": [3, 13, 16],
			"faces": {
				"north": {"uv": [6, 6, 9, 16], "texture": "#1"},
				"east": {"uv": [9, 6, 12, 16], "texture": "#1"},
				"south": {"uv": [0, 6, 3, 16], "texture": "#1", "cullface": "south"},
				"west": {"uv": [3, 6, 6, 16], "texture": "#1", "cullface": "west"}
			}
		},
		{
			"name": "frame7",
			"from": [3, 0, 0],
			"to": [13, 3, 3],
			"faces": {
				"north": {"uv": [9, 6, 12, 16], "rotation": 270, "texture": "#1", "cullface": "north"},
				"south": {"uv": [3, 6, 6, 16], "rotation": 270, "texture": "#1"},
				"up": {"uv": [6, 6, 9, 16], "rotation": 270, "texture": "#1"},
				"down": {"uv": [0, 6, 3, 16], "rotation": 270, "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "frame8",
			"from": [3, 13, 13],
			"to": [13, 16, 16],
			"faces": {
				"north": {"uv": [9, 6, 12, 16], "rotation": 270, "texture": "#1"},
				"south": {"uv": [3, 6, 6, 16], "rotation": 270, "texture": "#1", "cullface": "south"},
				"up": {"uv": [0, 6, 3, 16], "rotation": 270, "texture": "#1", "cullface": "up"},
				"down": {"uv": [6, 6, 9, 16], "rotation": 270, "texture": "#1"}
			}
		},
		{
			"name": "frame9",
			"from": [0, 0, 3],
			"to": [3, 3, 13],
			"faces": {
				"east": {"uv": [3, 6, 6, 16], "rotation": 270, "texture": "#1"},
				"west": {"uv": [9, 6, 12, 16], "rotation": 270, "texture": "#1", "cullface": "west"},
				"up": {"uv": [6, 6, 9, 16], "rotation": 180, "texture": "#1"},
				"down": {"uv": [0, 6, 3, 16], "rotation": 180, "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "frame10",
			"from": [3, 0, 13],
			"to": [13, 3, 16],
			"faces": {
				"north": {"uv": [3, 6, 6, 16], "rotation": 90, "texture": "#1"},
				"south": {"uv": [9, 6, 12, 16], "rotation": 90, "texture": "#1", "cullface": "south"},
				"up": {"uv": [6, 6, 9, 16], "rotation": 90, "texture": "#1"},
				"down": {"uv": [0, 6, 3, 16], "rotation": 90, "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "frame11",
			"from": [0, 3, 0],
			"to": [3, 13, 3],
			"faces": {
				"north": {"uv": [3, 6, 6, 16], "texture": "#1", "cullface": "north"},
				"east": {"uv": [6, 6, 9, 16], "texture": "#1"},
				"south": {"uv": [9, 6, 12, 16], "texture": "#1"},
				"west": {"uv": [0, 6, 3, 16], "texture": "#1", "cullface": "west"}
			}
		},
		{
			"name": "frame12",
			"from": [13, 3, 13],
			"to": [16, 13, 16],
			"faces": {
				"north": {"uv": [9, 6, 12, 16], "texture": "#1"},
				"east": {"uv": [0, 6, 3, 16], "texture": "#1", "cullface": "east"},
				"south": {"uv": [3, 6, 6, 16], "texture": "#1", "cullface": "south"},
				"west": {"uv": [6, 6, 9, 16], "texture": "#1"}
			}
		},
		{
			"name": "corner1",
			"from": [13, 13, 0],
			"to": [16, 16, 3],
			"faces": {
				"north": {"uv": [13, 3, 16, 0], "texture": "#1", "cullface": "north"},
				"east": {"uv": [13, 6, 10, 3], "texture": "#1", "cullface": "east"},
				"up": {"uv": [13, 3, 10, 0], "texture": "#1", "cullface": "up"}
			}
		},
		{
			"name": "frame1",
			"from": [3, 13, 0],
			"to": [13, 16, 3],
			"faces": {
				"north": {"uv": [3, 6, 6, 16], "rotation": 90, "texture": "#1", "cullface": "north"},
				"south": {"uv": [9, 6, 12, 16], "rotation": 90, "texture": "#1"},
				"up": {"uv": [0, 6, 3, 16], "rotation": 90, "texture": "#1", "cullface": "up"},
				"down": {"uv": [6, 6, 9, 16], "rotation": 90, "texture": "#1"}
			}
		},
		{
			"name": "frame2",
			"from": [13, 13, 3],
			"to": [16, 16, 13],
			"faces": {
				"east": {"uv": [3, 6, 6, 16], "rotation": 270, "texture": "#1", "cullface": "east"},
				"west": {"uv": [9, 6, 12, 16], "rotation": 270, "texture": "#1"},
				"up": {"uv": [0, 6, 3, 16], "rotation": 180, "texture": "#1", "cullface": "up"},
				"down": {"uv": [6, 6, 9, 16], "rotation": 180, "texture": "#1"}
			}
		},
		{
			"name": "frame3",
			"from": [13, 0, 3],
			"to": [16, 3, 13],
			"faces": {
				"east": {"uv": [9, 6, 12, 16], "rotation": 90, "texture": "#1", "cullface": "east"},
				"west": {"uv": [3, 6, 6, 16], "rotation": 90, "texture": "#1"},
				"up": {"uv": [6, 6, 9, 16], "texture": "#1"},
				"down": {"uv": [0, 6, 3, 16], "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "frame4",
			"from": [0, 13, 3],
			"to": [3, 16, 13],
			"faces": {
				"east": {"uv": [9, 6, 12, 16], "rotation": 90, "texture": "#1"},
				"west": {"uv": [3, 6, 6, 16], "rotation": 90, "texture": "#1", "cullface": "west"},
				"up": {"uv": [0, 6, 3, 16], "texture": "#1", "cullface": "up"},
				"down": {"uv": [6, 6, 9, 16], "texture": "#1"}
			}
		},
		{
			"name": "frame5",
			"from": [13, 3, 0],
			"to": [16, 13, 3],
			"faces": {
				"north": {"uv": [0, 6, 3, 16], "texture": "#1", "cullface": "north"},
				"east": {"uv": [3, 6, 6, 16], "texture": "#1", "cullface": "east"},
				"south": {"uv": [6, 6, 9, 16], "texture": "#1"},
				"west": {"uv": [9, 6, 12, 16], "texture": "#1"}
			}
		},
		{
			"name": "corner1Overlay",
			"from": [13, 13, 0],
			"to": [16, 16, 3],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1}
			}
		},
		{
			"name": "corner2",
			"from": [13, 13, 13],
			"to": [16, 16, 16],
			"faces": {
				"east": {"uv": [13, 3, 16, 0], "texture": "#1", "cullface": "east"},
				"south": {"uv": [13, 6, 10, 3], "texture": "#1", "cullface": "south"},
				"up": {"uv": [13, 0, 10, 3], "texture": "#1", "cullface": "up"}
			}
		},
		{
			"name": "corner2Overlay",
			"from": [13, 13, 13],
			"to": [16, 16, 16],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1}
			}
		},
		{
			"name": "corner3",
			"from": [0, 13, 0],
			"to": [3, 16, 3],
			"faces": {
				"north": {"uv": [13, 6, 10, 3], "texture": "#1", "cullface": "north"},
				"west": {"uv": [13, 3, 16, 0], "texture": "#1", "cullface": "west"},
				"up": {"uv": [10, 3, 13, 0], "texture": "#1", "cullface": "up"}
			}
		},
		{
			"name": "corner3Overlay",
			"from": [0, 13, 0],
			"to": [3, 16, 3],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1}
			}
		},
		{
			"name": "corner4",
			"from": [0, 13, 13],
			"to": [3, 16, 16],
			"faces": {
				"south": {"uv": [13, 3, 16, 0], "texture": "#1", "cullface": "south"},
				"west": {"uv": [13, 6, 10, 3], "texture": "#1", "cullface": "west"},
				"up": {"uv": [10, 0, 13, 3], "texture": "#1", "cullface": "up"}
			}
		},
		{
			"name": "corner4Overlay",
			"from": [0, 13, 13],
			"to": [3, 16, 16],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1}
			}
		},
		{
			"name": "corner5",
			"from": [13, 0, 0],
			"to": [16, 3, 3],
			"faces": {
				"north": {"uv": [10, 3, 13, 6], "texture": "#1", "cullface": "north"},
				"east": {"uv": [16, 0, 13, 3], "texture": "#1", "cullface": "east"},
				"down": {"uv": [13, 0, 10, 3], "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "corner5Overlay",
			"from": [13, 0, 0],
			"to": [16, 3, 3],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1}
			}
		},
		{
			"name": "corner6",
			"from": [13, 0, 13],
			"to": [16, 3, 16],
			"faces": {
				"east": {"uv": [10, 3, 13, 6], "texture": "#1", "cullface": "east"},
				"south": {"uv": [16, 0, 13, 3], "texture": "#1", "cullface": "south"},
				"down": {"uv": [13, 3, 10, 0], "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "corner6Overlay",
			"from": [13, 0, 13],
			"to": [16, 3, 16],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "east", "tintindex": 1}
			}
		},
		{
			"name": "corner7",
			"from": [0, 0, 0],
			"to": [3, 3, 3],
			"faces": {
				"north": {"uv": [16, 0, 13, 3], "texture": "#1", "cullface": "north"},
				"west": {"uv": [10, 3, 13, 6], "texture": "#1", "cullface": "west"},
				"down": {"uv": [10, 0, 13, 3], "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "corner7Overlay",
			"from": [0, 0, 0],
			"to": [3, 3, 3],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "north", "tintindex": 1}
			}
		},
		{
			"name": "corner8",
			"from": [0, 0, 13],
			"to": [3, 3, 16],
			"faces": {
				"south": {"uv": [10, 3, 13, 6], "texture": "#1", "cullface": "south"},
				"west": {"uv": [16, 0, 13, 3], "texture": "#1", "cullface": "west"},
				"down": {"uv": [10, 3, 13, 0], "texture": "#1", "cullface": "down"}
			}
		},
		{
			"name": "corner8Overlay",
			"from": [0, 0, 13],
			"to": [3, 3, 16],
			"faces": {
				"north": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"east": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"south": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"west": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"up": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1},
				"down": {"uv": [0, 0, 16, 16], "texture": "#6", "cullface": "south", "tintindex": 1}
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
    base["textures"]["6"] = f"mekanism:block/{tier}_corner_overlay"

    for n in range(64):
        output = copy.deepcopy(base)
        binary = format(n, "06b")
        for i, bit in enumerate(binary):
            if bit == "1":
                for obj in faces[i]:
                    output["elements"].append(obj)

        with open(f"{path}/{tier}/b{binary}.json", "w") as f:
            json.dump(output, f)
