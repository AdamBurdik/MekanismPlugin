package me.adamix.mekanism.block.persistence;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.Mekanism;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class BlockPersistenceService {
    private final Set<WorldPos> dirty = new HashSet<>();

    private final Logger log;
    private final Mekanism plugin;
    private final BlockInstanceService blockInstanceService;

    public void markDirty(@NotNull WorldPos pos) {
        dirty.add(pos);
    }

    public void periodicSave() {
        long start = System.currentTimeMillis();
        for (WorldPos pos : dirty) {
            saveComponents(pos);
        }
        dirty.clear();
        long end = System.currentTimeMillis();
        log.info("Saving blocks took {}ms", (end - start));
    }

    private void saveComponents(@NotNull WorldPos pos) {
        blockInstanceService.get(pos).ifPresent(instance -> {
            PersistentDataContainer pdc = new CustomBlockData(pos.resolveBlock(), plugin);
            instance.save(pdc);
        });
    }
}
