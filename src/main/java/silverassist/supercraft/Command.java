package silverassist.supercraft;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.menu.admin.SetCraft;
import silverassist.supercraft.menu.user.Crafting;

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
                    Util.sendPrefixMessage(p,"§c§l");
                }
                new SetCraft(plugin,p,id).open();
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
