package silverassist.supercraft;

import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.menu.user.Crafting;
import silverassist.supercraft.system.Recipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class SuperCraft extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        try{
            Files.createDirectories(Paths.get(this.getDataFolder()+"/data"));
        }catch (IOException e){
            Util.sendConsole("dataフォルダの作成に失敗しました", Util.MessageType.ERROR);
            e.printStackTrace();
            plugin.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        plugin = this;
        log = getLogger();
        Crafting crafting = new Crafting(plugin);
        new Command(plugin,crafting);

        Recipe.reloadAll();
    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
