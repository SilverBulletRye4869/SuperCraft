package silverassist.supercraft.menu.admin.craftLottery;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.CustomConfig;
import silverassist.supercraft.Util;

import java.util.HashSet;;
import java.util.Set;

public class ItemList {
    private final JavaPlugin plugin;
    private final Player P;
    private final String ID;
    private final YamlConfiguration YML;
    private final Set<ItemStack> existItemSet = new HashSet<>();

    public ItemList(JavaPlugin plugin, Player p, String id){
        this.plugin = plugin;
        this.P = p;
        this.ID =id;
        this.YML = CustomConfig.getYmlByID(id);
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,27, Util.PREFIX+"§d§l"+ID+"§a§lのマルチモード編集");
        ConfigurationSection yml_multiMode = YML.getConfigurationSection("item.multi");
        for(int i = 0;i<27 && yml_multiMode!=null;i++){
            int i_f = i;
            ItemStack item = yml_multiMode.getItemStack(i_f +".item",null);
            if(item == null)return;
            existItemSet.add(item);
            item = new ItemStack(item){{
                ItemMeta meta = getItemMeta();
                meta.getLore().add("§6§l比重: "+yml_multiMode.getInt(i_f +".weight"));
                setItemMeta(meta);
            }};
            inv.setItem(i,item);
        }
        P.openInventory(inv);
    }

    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;

            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null)return;
            e.setCancelled(true);
            switch (e.getClickedInventory().getType()){
                case PLAYER:
                    int dataSize = existItemSet.size();
                    if(dataSize>=27)return;
                    ItemStack item = e.getCurrentItem();
                    if(existItemSet.contains(item)){
                        Util.sendPrefixMessage(P,"§c§lそのアイテムは既に存在します");
                        break;
                    }
                    existItemSet.add(item);
                    YML.set("item.multi."+dataSize+".item",item);
                    YML.set("item.multi."+dataSize+".weight",1);
                    item = new ItemStack(item){{
                        ItemMeta meta = getItemMeta();
                        meta.getLore().add("§6§l比重: "+1);
                        setItemMeta(meta);
                    }};
                    P.getOpenInventory().setItem(dataSize,item);
                    break;
                case CHEST:
                    new Item(plugin,P,ID,e.getSlot()).open();
                    break;
            }
            CustomConfig.saveYmlByID(ID);
        }
    }
}
