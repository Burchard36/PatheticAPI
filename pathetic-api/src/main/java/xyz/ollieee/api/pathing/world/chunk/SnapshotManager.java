package xyz.ollieee.api.pathing.world.chunk;

import lombok.NonNull;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     * @param location the location to check
     * @return {@link PathBlock} the block or null if the chunk is not loaded
     */
    // @NonNull
    PathBlock getBlock(PathLocation location);
}
