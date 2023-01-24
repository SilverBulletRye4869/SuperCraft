package silverassist.supercraft.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.CustomConfig;
import silverassist.supercraft.Util;
import silverassist.supercraft.system.Check;
import silverassist.supercraft.system.Recipe;

import java.util.Arrays;

public class SetCraft {
    private final JavaPlugin plugin;
    private final Player P;
    private final String ID;
    public SetCraft(JavaPlugin plugin, Player p, String id){
        this.plugin = plugin;
        this.P = p;
        this.ID =id;
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,45, Util.PREFIX+"§r§d§l"+ID+"§a§lのクラフト編集");
        for(int i = 0;i<20;i++)inv.setItem(5 + 9*(i/4) + i%4, Util.GUI_BG);
        inv.setItem(24,Recipe.getCraftItem(ID));
        ItemStack[][] items = Recipe.getRawItem(ID);
        for(int i =0;i<items.length;i++)for(int j = 0;j<items[i].length;j++)inv.setItem(9 * i + j,items[i][j]);
        P.openInventory(inv);
    }

    private class listener implements Listener{
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            Inventory inv = e.getInventory();
            int[] side = Check.rectSlot(inv);
            if(side==null)return;
            e.getPlayer().sendMessage(Arrays.toString(side));
            YamlConfiguration yml = CustomConfig.getYmlByID(ID);
            for(int i=0;i<25;i++)yml.set("raw."+i/5+i%5,null);
            for(int x = side[0];x<side[1];x++){
                for(int y = side[2];y<side[3];y++){
                    ItemStack item = inv.getItem(x*9+y);
                    yml.set("raw."+(x - side[0])+(y - side[2]),item);
                }
            }
            yml.set("item",inv.getItem(24));
            yml.set("raw.h",side[1]-side[0]);
            yml.set("raw.w",side[3]-side[2]);
            CustomConfig.saveYmlByID(ID);
            Recipe.reload(ID);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()))return;
            if(e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            int slot = e.getSlot();
            if(slot%9 > 4 && slot!=24)e.setCancelled(true);
        }
    }
}
