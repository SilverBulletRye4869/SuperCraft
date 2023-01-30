package silverassist.supercraft;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.menu.admin.CraftEdit;
import silverassist.supercraft.menu.admin.CraftList;
import silverassist.supercraft.menu.user.Crafting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                break;



        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
            return null;
        }
    }
}
