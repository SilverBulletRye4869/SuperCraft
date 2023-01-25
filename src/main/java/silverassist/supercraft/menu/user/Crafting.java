package silverassist.supercraft.menu.user;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.Util;
import silverassist.supercraft.system.Check;
import silverassist.supercraft.system.Log;
import silverassist.supercraft.system.Recipe;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crafting {
    private final JavaPlugin plugin;
    private final Set<Player> isOpenSet = new HashSet<>();
    private final ItemStack RECHECK_BUTTON = Util.createItem(Material.CLOCK,"§e§lリチェック", List.of("§f§lレシピが正しいのにアイテムが","§f§l正しく出ないと思ったらこれ","§f§lを押してください。","§6§l再判定をします"));

    public Crafting(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }


    public void open(Player p){
        Inventory inv = Bukkit.createInventory(p,45, Util.PREFIX+"§d§lクラフティング画面");
        for(int i = 0;i<20;i++)inv.setItem(5 + 9*(i/4) + i%4, Util.GUI_BG);
        inv.setItem(24,Util.NULL_BG);
        inv.setItem(44,RECHECK_BUTTON);
        p.openInventory(inv);
        isOpenSet.add(p);
    }

    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!isOpenSet.contains(e.getPlayer()))return;
            isOpenSet.remove(e.getPlayer());
            if(!e.getView().getTitle().contains(Util.PREFIX))return;
            for(int slot : Util.getRectSlotPlaces(0,5,5)){
                ItemStack item = e.getInventory().getItem(slot);
                if(item == null)continue;
                e.getPlayer().getInventory().addItem(item);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){

            Player p = (Player) e.getWhoClicked();
            Inventory inv = e.getClickedInventory();
            if(!isOpenSet.contains(p) || inv == null || !inv.getType().equals(InventoryType.CHEST))return;
            int slot =e.getSlot();
            if(slot % 9 > 4)e.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                String id = Check.table(inv);
                if(id==null){
                    inv.setItem(24,Util.NULL_BG);return;
                }
                if(id.equals("__Err__")){
                    Util.sendPrefixMessage(p,"§cこの文章が表示されたら、運営にすぐにお知らせください。");
                    Util.sendPrefixMessage(p,"§4エラープラグイン: §6§lSuperCraft");
                    Util.sendPrefixMessage(p,"§4エラー内容: §6§lアイテムが被っています。詳細はコンソールを確認してください");
                    Util.sendPrefixMessage(p,"§4現在時刻: "+new Date());
                }
                ItemStack item = Recipe.getCraftItem(id);
                if(slot != 24)inv.setItem(24,item);
                else if(inv.getType().equals(InventoryType.CHEST) && slot == 24 && e.getCurrentItem()!=null){
                    if(e.getCurrentItem().equals(item)) {
                        p.getInventory().addItem(item);
                        for (int i : Util.getRectSlotPlaces(0, 5, 5)) inv.setItem(i, null);
                        Log.write(id,p,item,"single");
                    }
                    inv.setItem(24,Util.NULL_BG);
                }
            },1);
        }
    }

}
