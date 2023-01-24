package silverassist.supercraft.menu.user;

import org.bukkit.Bukkit;
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
import silverassist.supercraft.system.Recipe;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Crafting {
    private final JavaPlugin plugin;
    private final Set<Player> isOpenSet = new HashSet<>();

    public Crafting(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }


    public void open(Player p){
        Inventory inv = Bukkit.createInventory(p,45, Util.PREFIX+"§d§lクラフティング画面");
        for(int i = 0;i<20;i++)inv.setItem(5 + 9*(i/4) + i%4, Util.GUI_BG);
        inv.setItem(24,null);
        p.openInventory(inv);
        isOpenSet.add(p);
    }

    private class listener implements Listener {
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(isOpenSet.contains(e.getPlayer()))isOpenSet.remove(e.getPlayer());
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            Player p = (Player) e.getWhoClicked();
            Inventory inv = e.getClickedInventory();
            if(!isOpenSet.contains(p) || inv == null || !inv.getType().equals(InventoryType.CHEST))return;
            int slot =e.getSlot();
            if(slot % 9 > 4)e.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    String id = Check.table(inv);
                    if(id==null){
                        inv.setItem(24,null);return;
                    }
                    if(id.equals("__Err__")){
                        Util.sendPrefixMessage(p,"§cこの文章が表示されたら、運営にすぐにお知らせください。");
                        Util.sendPrefixMessage(p,"§4エラープラグイン: §6§lSuperCraft");
                        Util.sendPrefixMessage(p,"§4エラー内容: §6§lアイテムが被っています。詳細はコンソールを確認してください");
                        Util.sendPrefixMessage(p,"§4現在時刻: "+new Date());
                    }
                    ItemStack item = Recipe.getCraftItem(id);
                    if(slot % 9 < 5)inv.setItem(24,item);
                    else if(inv.getType().equals(InventoryType.CHEST) && slot == 24 && e.getCurrentItem()!=null){
                        if(e.getCurrentItem().equals(item)) {
                            p.getInventory().addItem(item);
                            for (int i : Util.getRectSlotPlaces(0, 5, 5)) inv.setItem(i, null);
                        }
                        inv.setItem(24,null);
                    }
                }
            },1);
        }
    }

}
