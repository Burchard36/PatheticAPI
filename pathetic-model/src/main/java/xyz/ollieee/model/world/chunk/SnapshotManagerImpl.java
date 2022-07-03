package xyz.ollieee.model.world.chunk;

import lombok.NonNull;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.DataPaletteBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
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

    // @NonNull
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
            /*
             * TODO: 27/04/2022 Make this thread safe
             * TODO: 01/07/2022 Needs to be tested
             */
            World world = BukkitConverter.toWorld(location.getPathWorld());
            if(!world.isChunkLoaded(chunkX, chunkZ))
                /*
                 * Returns null to indicate that the chunk is not loaded.
                 * NOTE: This is only supposed to be a temporary solution, since this breaks our null semantic.
                 */
                return null;
    
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
    
    private ChunkSnapshot retrieveChunkSnapshot(Chunk chunk) throws NoSuchFieldException, IllegalAccessException {
        
        CraftChunk craftChunk = (CraftChunk) chunk;
    
        Field field = craftChunk.getClass().getField("emptyBlockIDs");
        field.setAccessible(true);
    
        DataPaletteBlock<IBlockData> dataDataPaletteBlock = (DataPaletteBlock<IBlockData>) field.get(craftChunk);
        dataDataPaletteBlock.b();
        
        return craftChunk.getChunkSnapshot();
    }
}
