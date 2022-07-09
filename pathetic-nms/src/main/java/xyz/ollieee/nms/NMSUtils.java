package xyz.ollieee.nms;

import xyz.ollieee.api.pathing.world.chunk.ChunkSnapshotGrabber;
import xyz.ollieee.nms.v1_19.OneNineteenSnapshotGrabber;

public class NMSUtils {

    private final ChunkSnapshotGrabber chunkSnapshotGrabber;

    public NMSUtils(final String version) {
        switch (version) {
            case "19":
                chunkSnapshotGrabber = new OneNineteenSnapshotGrabber();
                break;
            default:
                throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }

    public ChunkSnapshotGrabber getChunkSnapshotGrabber() {
        return chunkSnapshotGrabber;
    }
}
