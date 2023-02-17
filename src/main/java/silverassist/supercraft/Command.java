package silverassist.supercraft;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.menu.admin.CraftEdit;
import silverassist.supercraft.menu.admin.CraftList;
import silverassist.supercraft.menu.admin.craftLottery.Item;
import silverassist.supercraft.menu.user.Crafting;
import silverassist.supercraft.system.Recipe;

import java.util.List;

public class Command implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Crafting crafting;

    public Command(JavaPlugin plugin,Crafting crafting){
        this.plugin = plugin;
        this.crafting = crafting;
        PluginCommand command = plugin.getCommand("supercraft");
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length<1){
            return true;
        }
        Player p = (Player) sender;
        String id = args.length > 1 ? args[1] : null;
        switch (args[0]){

            case "craft":
                crafting.open(p);
                break;
            case "create":
                if(!CustomConfig.existYml(id))CustomConfig.createYmlByID(id);
            case "edit":
                if(!CustomConfig.existYml(id)){
                    Util.sendPrefixMessage(p,"§c§lそのレシピidは存在しません");
                    return true;
                }
                new CraftEdit(plugin,p,id).open();
                break;
            case "list":
                new CraftList(plugin,p,List.of(args).contains("-icon"),List.of(args).contains("-enable")).open(0);
                break;
            case "setmessage":
                if(args.length<4 || !CustomConfig.existYml(id) || !args[2].matches("\\d+"))return true;
                int itemID = Integer.parseInt(args[2]);
                if(itemID>26||itemID<0)return true;
                CustomConfig.getYmlByID(id).set("item.multi."+itemID+".message",args[3]);
                CustomConfig.saveYmlByID(id);
                new Item(plugin,p,id,itemID).open();
                break;
            case "reload":
                if(id==null)plugin.reloadConfig();
                else{
                    if(!CustomConfig.existYml(id))return true;
                    Recipe.reload(id);
                }
                break;
            case "get":
                int dur = (id==null || !args[1].matches("\\d+")) ? 1 : Integer.parseInt(args[1]) ;
                ItemStack item = new NBTItem(Util.createItem(Material.CRAFTING_TABLE,"§b§lSuperCraft"))
                    {{set("supercraft",dur);}}.getItem();
                p.getInventory().addItem(item);






        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
            if(!sender.isOp())return null;
            switch (args.length){
                case 1:
                    return List.of("craft","create","edit","list","reload","get");
                case 2:
                    switch (args[0]){
                        case "edit":
                        case "reload":
                            return Recipe.getRecipeList(args[1]);
                    }
            }
            return null;
        }
    }
}
