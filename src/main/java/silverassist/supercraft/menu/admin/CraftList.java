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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.CustomConfig;
import silverassist.supercraft.Util;
import silverassist.supercraft.system.Recipe;

import java.util.List;

public class CraftList {
    private final JavaPlugin plugin;
    private final Player P;
    private final List<String> RECIPE_DATAS;
    private final boolean OUTPUT_ICON;
    private final boolean OUTPUT_ENABLE;
    private int page;

    public CraftList(JavaPlugin plugin, Player p, boolean outputIcon, boolean outputEnable){
        this.plugin = plugin;
        this.P = p;
        this.RECIPE_DATAS =Recipe.getRecipeList();
        this.OUTPUT_ICON = outputIcon;
        this.OUTPUT_ENABLE = outputEnable;
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(int page){
        this.page = page;
        Inventory inv = Bukkit.createInventory(P, 54,Util.PREFIX+"§d§lレシピ一覧");
        P.openInventory(inv);
        unregisterCancel = false;
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
            for(int i = 45*page;i<Math.min(45*(page+1),RECIPE_DATAS.size());i++){
                String id = RECIPE_DATAS.get(i);
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                if(OUTPUT_ICON || OUTPUT_ENABLE){
                    YamlConfiguration yml = CustomConfig.getYmlByID(id);
                    if(OUTPUT_ICON){
                        item = new ItemStack(yml.getItemStack("item.single",item));
                        meta = item.getItemMeta();
                    }
                    if(OUTPUT_ENABLE){
                        List<String> lores = meta.getLore();
                        lores.add(yml.getBoolean("isEnable") ? "§a§lクラフト可能" : "§c§lクラフト不可");
                        meta.setLore(lores);
                    }
                }
                meta.setDisplayName(id);
                item.setItemMeta(meta);
                inv.setItem(i%45,item);
            }
            for(int slot : Util.getRectSlotPlaces(45,9,1))inv.setItem(slot,Util.GUI_BG);
            if(page>0)inv.setItem(45,Util.createItem(Material.RED_STAINED_GLASS_PANE,"§c§l戻る"));
            if(page< RECIPE_DATAS.size()-1 / 45)inv.setItem(53,Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l次へ"));
        });

    }

    private boolean unregisterCancel = false;
    private class listener implements Listener {
        @EventHandler
        public void onInvenotryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()) || unregisterCancel)return;
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            int slot = e.getSlot();
            if(slot < 45){
                int index = page * 45 + slot;
                new CraftEdit(plugin,P,RECIPE_DATAS.get(index)).open();
            }else{
                Material itemType = e.getCurrentItem().getType();
                if(itemType.equals(Material.LIGHT_BLUE_STAINED_GLASS_PANE))return;
                unregisterCancel = true;
                if(itemType.equals(Material.RED_STAINED_GLASS_PANE))page--;
                else if(itemType.equals(Material.LIME_STAINED_GLASS_PANE))page++;
                open(page);
            }
        }
    }
}
