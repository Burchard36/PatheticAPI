package xyz.ollieee.pathing;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.Pathetic;

public class PatheticPlugin extends JavaPlugin {
    
    @Override
    public void onLoad() {
        Pathetic.initialize(this);
    }
}