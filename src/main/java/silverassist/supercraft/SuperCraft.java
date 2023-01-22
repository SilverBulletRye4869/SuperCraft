package silverassist.supercraft;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class SuperCraft extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        log = getLogger();

    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
