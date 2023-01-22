package silverassist.supercraft.menu.admin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SetCraft {
    private final JavaPlugin plugin;
    private final Player P;
    public SetCraft(JavaPlugin plugin, Player p){
        this.plugin = plugin;
        this.P = p;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        
    }

    private class listener implements Listener{
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;

            HandlerList.unregisterAll(this);
        }
    }
}
