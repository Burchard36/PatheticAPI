package xyz.oli;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.PathfinderFactory;
import xyz.oli.pathing.SnapshotManager;

@UtilityClass
public class PathingAPI {

    private MaterialParser parser = null;
    private SnapshotManager snapshotManager = null;

    public Pathfinder instantiateNewPathfinder() {
        
        RegisteredServiceProvider<PathfinderFactory> registration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);
        
        if(registration == null)
            throw new IllegalStateException();
        
        return registration.getProvider().newPathfinder();
    }
    
    public void setFields(@NonNull MaterialParser parser, @NonNull SnapshotManager snapshotManager) {
        
        if (PathingAPI.parser != null || PathingAPI.snapshotManager != null)
            throw new UnsupportedOperationException("Cannot redefine singleton MaterialParser");
        
        PathingAPI.parser = parser;
        PathingAPI.snapshotManager = snapshotManager;
    }
    
    public MaterialParser getParser() {
        return parser;
    }

    public SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }
}