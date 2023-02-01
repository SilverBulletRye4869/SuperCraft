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
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Recipe {

    private static final JavaPlugin plugin = SuperCraft.getInstance();
    private static Map<String,ItemStack[][]> rawItems = new HashMap<>();
    private static Map<String,ItemStack> craftItems = new HashMap<>();
    private static HashSet<String> multiModes = new HashSet<>();
    public static final Predicate<String> isMulti = id -> multiModes.contains(id);
    private static Map<String,LinkedHashMap<Integer,ItemStack>> multiModeItems = new HashMap<>();
    private static Map<String,LinkedList<String>> multiModeMsgs = new HashMap<>();
    public static final BiFunction<String,Integer,String> getMsg = (id,index) -> multiModeMsgs.get(id).get(index);


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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> getRecipeList().forEach(Recipe::reload));
    }

    public static boolean reload(String id){
        delete(id);
        if(!CustomConfig.existYml(id))return false;
        YamlConfiguration yml = CustomConfig.getYmlByID(id);
        if(!yml.getBoolean("isEnable",true))return false;

        //クラフト素材のreload
        int w = yml.getInt("raw.w",5);
        int h = yml.getInt("raw.h",5);
        ItemStack[][] raws = new ItemStack[h][w];
        for(int i = 0;i<h;i++){
            for(int j = 0;j<w;j++){
                raws[i][j] = yml.getItemStack("raw."+i+j,new ItemStack(Material.AIR,0));
            }
        }

        //クラフト先のreload
        //シングルクラフト
        craftItems.put(id,yml.getItemStack("item.single"));
        rawItems.put(id,raws);
        //マルチクラフト
        if(yml.getBoolean("isMulti")){
            multiModes.add(id);
            LinkedHashMap<Integer,ItemStack> multiModeItem = new LinkedHashMap<>();
            LinkedList multiModeMsg = new LinkedList();
            int max = 0;
            for(int i = 0;i<27;i++){
                ItemStack item = yml.getItemStack("item.multi."+i+".item");
                if(item == null)break;
                max += yml.getInt("item.multi."+i+".weight");
                multiModeItem.put(max,item);
                multiModeMsg.add(yml.getString("item.multi."+i+".message"));
            }
            multiModeItems.put(id,multiModeItem);
            multiModeMsgs.put(id,multiModeMsg);
        }
        return true;
    }

    public static LinkedHashMap<Integer,ItemStack> getMultiModeItem(String id){
        if(multiModeItems.containsKey(id))return multiModeItems.get(id);
        return null;
    }

    public static void delete(String id){
        rawItems.remove(id);
        multiModes.remove(id);
        multiModeItems.remove(id);
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
