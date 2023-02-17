package silverassist.supercraft.system;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.menu.user.Crafting;

public class OpenCraftingGUI implements Listener {
    private final Crafting craftingGUI;
    public OpenCraftingGUI(JavaPlugin plugin, Crafting craftingGUI){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        this.craftingGUI = craftingGUI;
    }
    @EventHandler
    public void open(PlayerInteractEvent e){
        if(!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)))return;
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(item);
        if(!nbtItem.hasKey("supercraft") || nbtItem.getInteger("supercrtaft") < 1)return;
        craftingGUI.open(p);
        e.setCancelled(true);
    }

}
