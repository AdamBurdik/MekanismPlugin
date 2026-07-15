# Mekanism Plugin

This project is remake of infamous mod [Mekanism](https://github.com/mekanism/Mekanism) as server side plugin for papermc servers.

## How does it work?

Plugin utilizes resource pack features to achieve most of the stuff.

Custom blocks are done by spawning item display entity with item containing item_model component.

The actual simulation (electricity generation, item processing) is done off main thread using prebuilt graphs of networks. 

Networks are updated only on block change.

This allows to run simulation with thousands of blocks without impact on main thread.

## Architecture

Networks are built only from block adjacency, no manual storage of connections.

Each block has one of three roles:
- **Transporter** — cables, pipes. They form the body of the network.
- **Producer** — generators, import chests.
- **Consumer** — machines, export chests.

Energy cubes can be both a producer and a consumer at the same time.

Each side of a block is its own port. A port has a type (input, output or disabled) and belongs to one network category (energy, item, and so on). This is what allows something like an Energy Cube to sit between generators and machines without electricity flowing straight through it.

Block behavior is built from components instead of big inheritance trees. A generator, for example, is just an energy component combined with a generation strategy (solar, wind, combustion, and so on).

There are two separate tick loops, both running off main thread:
- **Block tick** — for blocks that produce or process on their own, like generators or crushers.
- **Network tick** — moves energy/items between ports that are already part of a network. Skips ports in unloaded chunks.

Placing a cable merges networks together. Breaking a cable can split a network into multiple ones. This is checked using BFS.

## Status

Early development.

Placement and network connecting is done for universal cables, energy cubes and solar generator.

# Credits
- Original mekanism: https://github.com/mekanism/Mekanism
- Classic mekanism textures: https://www.curseforge.com/minecraft/texture-packs/classic-mekanism-textures

# Licence
- [MIT](./LICENCE)