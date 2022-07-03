package xyz.ollieee.model.world.chunk;

import lombok.NonNull;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftChunk;
import xyz.ollieee.Pathetic;
import xyz.ollieee.model.world.WorldDomain;
import xyz.ollieee.api.pathing.world.chunk.SnapshotManager;
import xyz.ollieee.util.ChunkUtils;
import xyz.ollieee.utils.BukkitConverter;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SnapshotManagerImpl implements SnapshotManager {

    private final Map<UUID, WorldDomain> snapshots = new HashMap<>();
    private static final Field blockIDField;

    static {
        try {
            blockIDField = CraftChunk.class.getDeclaredField("emptyBlockIDs");
            blockIDField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public PathBlock getBlock(@NonNull PathLocation location) {
        
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        long key = ChunkUtils.getChunkKey(chunkX, chunkZ);

        if (snapshots.containsKey(location.getPathWorld().getUuid())) {
            
            WorldDomain worldDomain = snapshots.get(location.getPathWorld().getUuid());
            Optional<ChunkSnapshot> snapshot = worldDomain.getSnapshot(key);
            
            if (snapshot.isPresent())
                return new PathBlock(location, BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(snapshot.get(), location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16)));
        }
        
        return fetchAndGetBlock(location, chunkX, chunkZ, key);
    }

    private PathBlock fetchAndGetBlock(@NonNull PathLocation location, int chunkX, int chunkZ, long key) {
        
        try {
            
            World world = BukkitConverter.toWorld(location.getPathWorld());
    
            // TODO: 03/07/2022 The chunk is getting loaded if its unloaded. We want to avoid that
            Chunk chunk = world.getChunkAt(chunkX, chunkZ);

            ChunkSnapshot chunkSnapshot = retrieveChunkSnapshot(chunk);

            addSnapshot(location, key, chunkSnapshot);
            
            PathBlockType pathBlockType = BukkitConverter.toPathBlockType(ChunkUtils.getMaterial(chunkSnapshot, location.getBlockX() - chunkX * 16, location.getBlockY(), location.getBlockZ() - chunkZ * 16));
            return new PathBlock(location, pathBlockType);
            
        } catch (Exception e) {
            
            Pathetic.getPluginLogger().warning("Error fetching Block: " + e.getMessage());
            return new PathBlock(location, PathBlockType.SOLID);
        }
    }

    private void addSnapshot(@NonNull PathLocation location, long key, @NonNull ChunkSnapshot snapshot) {
        
        if (!snapshots.containsKey(location.getPathWorld().getUuid())) snapshots.put(location.getPathWorld().getUuid(), new WorldDomain());
        
        WorldDomain worldDomain = snapshots.get(location.getPathWorld().getUuid());
        worldDomain.addSnapshot(key, snapshot);
    
        Bukkit.getScheduler().runTaskLater(Pathetic.getPluginInstance(), () -> worldDomain.removeSnapshot(key), 1200L);
    }
    
    private ChunkSnapshot retrieveChunkSnapshot(Chunk chunk) {

        try {
            CraftChunk craftChunk = (CraftChunk) chunk;

            if (Bukkit.isPrimaryThread())
                return craftChunk.getChunkSnapshot();

            PalettedContainer<BlockState> dataDataPaletteBlock = (PalettedContainer<BlockState>) blockIDField.get(craftChunk);

            dataDataPaletteBlock.release();
            dataDataPaletteBlock.acquire();
            ChunkSnapshot chunkSnapshot = craftChunk.getChunkSnapshot();
            dataDataPaletteBlock.release();

            return chunkSnapshot;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Pathetic.getPluginLogger().warning("Error fetching Chunk Snapshot: " + e.getMessage());
            return null;
        }
    }
}
