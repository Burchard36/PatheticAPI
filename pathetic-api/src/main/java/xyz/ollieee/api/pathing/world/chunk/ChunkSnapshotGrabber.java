package xyz.ollieee.api.pathing.world.chunk;

import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

public interface ChunkSnapshotGrabber {

    ChunkSnapshot getSnapshot(World world, int chunkX, int chunkZ);
}
