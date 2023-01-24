package silverassist.supercraft.system;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private static Map<String,ItemStack[][]> rawItems = new HashMap<>();
    private static Map<String,ItemStack> craftItems = new HashMap<>();

    public static Map<String,ItemStack[][]> getRawItems(){
        return rawItems;
    }
    public static ItemStack[][] getRawItem(String id){
        if(!rawItems.containsKey(id)){
            if(!reload(id))return null;
        }
        return rawItems.get(id);
    }

    public static ItemStack getCraftItem(String id){
        if(id.equals(null))return null;
        if(!craftItems.containsKey(id)){
            if(!reload(id))return null;
        }
        return craftItems.get(id);

    }

    public static void reloadAll(){
        rawItems.clear();
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
        if(!CustomConfig.existYml(id))return false;
        YamlConfiguration yml = CustomConfig.getYmlByID(id);
        int w = yml.getInt("raw.w",5);
        int h = yml.getInt("raw.h",5);
        ItemStack[][] raws = new ItemStack[h][w];
        for(int i = 0;i<h;i++){
            for(int j = 0;j<h;j++){
                raws[i][j] = yml.getItemStack("raw."+i+j,new ItemStack(Material.AIR,0));
            }
        }
        craftItems.put(id,yml.getItemStack("item"));
        rawItems.put(id,raws);
        return true;
    }

    public static void delete(String id){
        if(rawItems.containsKey(id)) rawItems.remove(id);
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
