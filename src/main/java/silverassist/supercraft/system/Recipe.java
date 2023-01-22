package silverassist.supercraft.system;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.supercraft.CustomConfig;
import silverassist.supercraft.SuperCraft;
import silverassist.supercraft.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Recipe {
    private static final JavaPlugin plugin = SuperCraft.getInstance();
    private static Map<String,ItemStack[]> rawMaterials = new HashMap<>();

    public static Map<String,ItemStack[]> getRawMaterials(){
        return rawMaterials;
    }

    public static void reloadAll(){
        rawMaterials.clear();
        Bukkit.getScheduler().runTaskAsynchronously(plugin,new Runnable(){
            @Override
            public void run() {
                getRecipeList().forEach(id->{
                    reload(id);
                });
            }
        });
    }

    public static boolean reload(String id){
        delete(id);
        ItemStack[] raws = new ItemStack[25];
        if(!CustomConfig.existYml(id))return false;
        YamlConfiguration y = CustomConfig.getYmlByID(id);
        for(int i = 0;i<25;i++){
            ItemStack item = y.getItemStack("raw."+i);
            if(item==null)break;
            else raws[i] =y.getItemStack("raw."+i);
        }
        if(raws[0] == null)return false;
        rawMaterials.put(id,raws);
        return true;
    }

    public static void delete(String id){
        if(rawMaterials.containsKey(id))rawMaterials.remove(id);
    }


    public static List<String> getRecipeList(){return getRecipeList("");}
    public static List<String> getRecipeList(String startRegex){
        Stream<Path> paths;
        try{
            paths = Files.list(Paths.get(plugin.getDataFolder()+"/data"));
        }catch (IOException e){
            Util.sendConsole("dataフォルダの取得に失敗しました", Util.MessageType.ERROR);
            e.printStackTrace();
            return null;
        }
        List<String> fileNames = new ArrayList<>();
        paths.forEach(e->{
            String fileName = e.getFileName().toString();
            if(fileName.matches("^"+startRegex+".*\\.yml$"))fileNames.add(fileName.replaceAll("\\.yml$",""));
        });
        return fileNames;
    }
}
