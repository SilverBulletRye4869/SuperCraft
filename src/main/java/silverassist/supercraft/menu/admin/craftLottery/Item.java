package silverassist.supercraft.menu.admin.craftLottery;

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
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.CustomConfig;
import silverassist.supercraft.Util;

import java.util.List;

public class Item {
    private final JavaPlugin plugin;
    private final Player P;
    private final String CRAFT_ID;
    private final int ITEM_ID;
    private final YamlConfiguration YML;

    public Item(JavaPlugin plugin, Player p, String craftID, int itemID){
        this.plugin = plugin;
        this.P = p;
        this.CRAFT_ID = craftID;
        this.ITEM_ID = itemID;
        this.YML = CustomConfig.getYmlByID(craftID);
        p.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }


    public void open(){
        Inventory inv = Bukkit.createInventory(P,27, Util.PREFIX+"§d§l"+CRAFT_ID+"/"+ITEM_ID+"§aの編集");
        Util.invFill(inv);
        inv.setItem(11,Util.createItem(Material.MAP,"§6§l比重を設定", List.of("§f§l現在: "+YML.getInt("item.multi."+ITEM_ID+".weight"))));
        inv.setItem(15,Util.createItem(Material.PAPER,"§f§l当選時メッセージ",List.of("§e"+YML.getString("item.multi."+ITEM_ID+".message"))));
        inv.setItem(26,Util.createItem(Material.LAVA_BUCKET,"§c§lこれを削除"));
        Bukkit.getScheduler().runTaskLater(plugin,()-> P.openInventory(inv),1);
    }


    private boolean isBack = true;
    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            if(isBack)new ItemList(plugin,P,CRAFT_ID).open();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            switch (e.getSlot()){
                case 11:
                    isBack = false;
                    new SetNum(P,CRAFT_ID,"item.multi."+ITEM_ID+".weight",1).open();
                    break;
                case 15:
                    for(int i = 0;i<20;i++)P.sendMessage("");
                    Util.sendPrefixMessage(P,"§a§lメッセージを変更するには以下のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/supercraft setmessage "+CRAFT_ID+" "+ITEM_ID+" <メッセージ>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/supercraft setmessage "+CRAFT_ID+" "+ITEM_ID+" ");
                    isBack = false;
                    P.closeInventory();
                    break;
                case 26:
                    for(int i = ITEM_ID;i<27;i++)YML.set("item.multi."+i,YML.get("item.multi."+(i+1)));
                    CustomConfig.saveYmlByID(CRAFT_ID);
                    P.closeInventory();
                    break;
            }

        }
    }
}
