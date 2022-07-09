package xyz.ollieee.nms;

import xyz.ollieee.api.pathing.world.chunk.ChunkSnapshotGrabber;

import xyz.ollieee.nms.v1_17.OneSeventeenSnapshotGrabber;
import xyz.ollieee.nms.v1_18.OneEighteenSnapshotGrabber;
import xyz.ollieee.nms.v1_19.OneNineteenSnapshotGrabber;

public class NMSUtils {

    private final ChunkSnapshotGrabber chunkSnapshotGrabber;

    public NMSUtils(final String version) {
        switch (version) {
            case "19":
                chunkSnapshotGrabber = new OneNineteenSnapshotGrabber();
                break;
            case "18":
                chunkSnapshotGrabber = new OneEighteenSnapshotGrabber();
                break;
            case "17":
                chunkSnapshotGrabber = new OneSeventeenSnapshotGrabber();
                break;
            default:
                throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }

    public ChunkSnapshotGrabber getChunkSnapshotGrabber() {
        return chunkSnapshotGrabber;
    }
}
