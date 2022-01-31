package xyz.oli.pathing;

import org.bukkit.Location;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;

import org.jetbrains.annotations.NotNull;

public class PathfinderOptions {

    private final Location start;
    private final Location target;
    private final boolean asyncMode;
    private final PathfinderStrategy strategy;

    public PathfinderOptions(Location start, Location target, boolean asyncMode, PathfinderStrategy strategy) {
        this.start = start;
        this.target = target;
        this.asyncMode = asyncMode;
        this.strategy = strategy;
    }

    public PathfinderOptions(@NotNull PathfinderOptionsBuilder builder) {
        this.start = builder.start;
        this.target = builder.target;
        this.asyncMode = builder.asyncMode;
        this.strategy = builder.strategy;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getTarget() {
        return this.target;
    }

    public boolean isAsyncMode() {
        return this.asyncMode;
    }

    public PathfinderStrategy getStrategy() {
        return this.strategy;
    }
}