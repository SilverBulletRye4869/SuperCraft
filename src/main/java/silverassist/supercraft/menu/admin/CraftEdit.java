package silverassist.supercraft.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import silverassist.supercraft.menu.admin.craftLottery.ItemList;
import silverassist.supercraft.system.Check;
import silverassist.supercraft.system.Recipe;

import java.util.function.Function;


public class CraftEdit {
    private final JavaPlugin plugin;
    private final Player P;
    private final String ID;
    private final YamlConfiguration YML;
    private final Function<String,ItemStack> GUI_ENABLE = (name) -> Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l"+name);
    private final Function<String,ItemStack> GUI_DISABLE = (name) ->Util.createItem(Material.RED_STAINED_GLASS_PANE,"§c§l"+name);

    public CraftEdit(JavaPlugin plugin, Player p, String id){
        this.plugin = plugin;
        this.P = p;
        this.ID =id;
        this.YML = CustomConfig.getYmlByID(id);
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,45, Util.PREFIX+"§r§d§l"+ID+"§a§lのクラフト編集");
        for(int slot:Util.getRectSlotPlaces(5,4,5))inv.setItem(slot, Util.GUI_BG);
        inv.setItem(24,YML.getItemStack("item.single"));
        for(int i =0;i<25;i++)inv.setItem(9 * (i/5) + i%5, YML.getItemStack("raw."+i/5+i%5));

        inv.setItem(42,Util.createItem(Material.CHEST,"§6§lマルチクラフトのアイテム編集"));
        inv.setItem(43,YML.getBoolean("isMulti",false) ? GUI_ENABLE.apply("マルチモード有効") : GUI_DISABLE.apply("マルチモード無効"));
        inv.setItem(44,YML.getBoolean("isEnable",true) ? GUI_ENABLE.apply("製作可能") : GUI_DISABLE.apply("製作不可"));
        Bukkit.getScheduler().runTaskLater(plugin,()-> P.openInventory(inv),1);
    }

    private class listener implements Listener{
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            Inventory inv = e.getInventory();
            int[] side = Check.rectSlot(inv);
            if(side==null)return;
            YamlConfiguration yml = CustomConfig.getYmlByID(ID);
            for(int i=0;i<25;i++)yml.set("raw."+i/5+i%5,null);
            for(int x = side[0];x<side[1];x++){
                for(int y = side[2];y<side[3];y++){
                    ItemStack item = inv.getItem(x*9+y);
                    yml.set("raw."+(x - side[0])+(y - side[2]),item);
                }
            }
            yml.set("item.single",inv.getItem(24));
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

            switch (slot){
                case 42:
                    new ItemList(plugin,P,ID).open();
                    break;
                case 43:
                case 44:
                    boolean toEnable = e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE;
                    if(slot == 43) {
                        e.getClickedInventory().setItem(43, toEnable ? GUI_ENABLE.apply("マルチモード有効") : GUI_DISABLE.apply("マルチモード無効"));
                        CustomConfig.getYmlByID(ID).set("isMulti", toEnable);
                    }else{
                        e.getClickedInventory().setItem(44, toEnable ? GUI_ENABLE.apply("製作可能") : GUI_DISABLE.apply("製作不可"));
                        CustomConfig.getYmlByID(ID).set("isEnable", toEnable);
                    }
                    CustomConfig.saveYmlByID(ID);
                    break;
            }
        }
    }
}
