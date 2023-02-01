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
import silverassist.supercraft.SuperCraft;
import silverassist.supercraft.Util;
import silverassist.supercraft.system.Check;
import silverassist.supercraft.system.Log;
import silverassist.supercraft.system.Recipe;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Crafting {
    private final JavaPlugin plugin = SuperCraft.getInstance();
    private final Set<Player> isOpenSet = new HashSet<>();
    private final ItemStack RECHECK_BUTTON = Util.createItem(Material.CLOCK,"§e§lリチェック", List.of("§f§lレシピが正しいのにアイテムが","§f§l正しく出ないと思ったらこれ","§f§lを押してください。","§6§l再判定をします"));
    private final Supplier<String> DEFAULT_MESSAGE = ()-> plugin.getConfig().getString("default_craft_message","§a§lクラフト成功！");
    private static final BiFunction<String,ItemStack,String> editMsg =
            (msg,item) -> msg.replaceAll("%item%",item.getType().toString()).replaceAll("%name%",item.getItemMeta().getDisplayName());


    public Crafting(){
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }


    public void open(Player p){
        Inventory inv = Bukkit.createInventory(p,45, Util.PREFIX+"§d§lクラフティング画面");
        for(int i = 0;i<20;i++)inv.setItem(5 + 9*(i/4) + i%4, Util.GUI_BG);
        inv.setItem(24,Util.NULL_BG);
        inv.setItem(44,RECHECK_BUTTON);
        isOpenSet.add(p);
        Bukkit.getScheduler().runTaskLater(plugin,()-> p.openInventory(inv),1);

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
                    return;
                }
                ItemStack item = Recipe.getCraftItem(id) == null ? Util.NULL_BG : Recipe.getCraftItem(id);
                if(slot != 24)inv.setItem(24,item);
                else if(inv.getType().equals(InventoryType.CHEST) && slot == 24 && e.getCurrentItem()!=null){
                    if(e.getCurrentItem().equals(item)) {
                        String typeID = "single";
                        String msg = null;
                        if(Recipe.isMulti.test(id)){
                            LinkedHashMap<Integer,ItemStack> items = Recipe.getMultiModeItem(id);
                            LinkedList<Integer> mapKeySetList = new LinkedList<>(items.keySet());
                            int randomMax = mapKeySetList.getLast();

                            double hit =Math.random() * randomMax;
                            for(int i : mapKeySetList){
                                if(hit >= i)continue;
                                int index = mapKeySetList.indexOf(i);
                                typeID = "multi."+index;
                                item = items.get(i);
                                if((msg = Recipe.getMsg.apply(id, index))!=null)msg = editMsg.apply(msg,item);
                                break;
                            }
                        }
                        p.getInventory().addItem(item);
                        Util.sendPrefixMessage(p,msg == null ? DEFAULT_MESSAGE.get() : msg);
                        ItemStack[][] raw_items =Recipe.getRawItem(id);
                        int[] side = Check.rectSlot(inv);
                        for (int index : Util.getRectSlotPlaces(0, 5, 5)){
                            ItemStack invItem = inv.getItem(index);
                            if(invItem == null)continue;
                            inv.setItem(index, new ItemStack(invItem){{setAmount(getAmount()-raw_items[index/9-side[0]][(index-side[2])%9].getAmount());}});

                        }
                        Log.write(id,p,item,typeID);

                    }

                    Bukkit.getScheduler().runTaskLater(plugin,()->{
                        String recheckResult;
                        ItemStack next = null;
                        if((recheckResult = Check.table(inv))!=null)next = Recipe.getCraftItem(recheckResult);
                        inv.setItem(24, next == null ? Util.NULL_BG : next);
                    },1);
                }
            },1);
        }
    }


}
